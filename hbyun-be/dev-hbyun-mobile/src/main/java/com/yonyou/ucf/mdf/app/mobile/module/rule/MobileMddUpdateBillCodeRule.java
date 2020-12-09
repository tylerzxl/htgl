package com.yonyou.ucf.mdf.app.mobile.module.rule;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.uap.billcode.BillCodeContext;
import com.yonyou.uap.billcode.BillCodeException;
import com.yonyou.uap.billcode.BillCodeObj;
import com.yonyou.uap.billcode.service.IBillCodeComponentService;
import com.yonyou.uap.billcode.service.IBillCodeValidate;
import com.yonyou.ucf.mdd.common.exceptions.MddBaseException;
import com.yonyou.ucf.mdd.common.exceptions.MddRuleException;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.common.utils.PartitionUtils;
import com.yonyou.ucf.mdd.core.utils.BizObjUtils;
import com.yonyou.ucf.mdd.uimeta.context.UIMetaSDKContext;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imeta.biz.base.MetaUtils;
import org.imeta.core.model.Entity;
import org.imeta.core.model.Property;
import org.imeta.orm.base.BizObject;
import org.imeta.orm.base.EntityStatus;
import org.imeta.orm.base.PartitionContext;
import org.imeta.spring.support.db.UniqueCheckWalker;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/28 7:33 下午
 */
@Slf4j
@NoArgsConstructor
@Component("mobileMddUpdateBillCodeRule")
public class MobileMddUpdateBillCodeRule extends AbstractMobileRule {

    @Override
    public <T> RuleExecuteResult execute(RuleContext ruleContext, T... ts) {
        IBillCodeComponentService componentService = UIMetaSDKContext.getBean(IBillCodeComponentService.class);
        List<String> usedBillCode = new ArrayList<>();
        UIMetaBaseInfo uiMetaBaseInfo = ruleContext.getUiMetaBaseInfo();
        String fullName = uiMetaBaseInfo.getFullname();
        String billNum = uiMetaBaseInfo.getBillnum();

        billNum = findUnifyBillNum(billNum);

        try {
            processMobileUpdateBillCode(ruleContext, componentService, usedBillCode, uiMetaBaseInfo, fullName, billNum);
        } catch (Exception e) {
            log.error("exception when process mobile bill code rule", e);
        }

        return new RuleExecuteResult();
    }

    private boolean processMobileUpdateBillCode(RuleContext ruleContext, IBillCodeComponentService componentService, List<String> usedBillCode,
                                                UIMetaBaseInfo uiMetaBaseInfo, String fullName, String billNum) throws Exception {
        String tenantId = ruleContext.getTenantId().toString();
        Entity entity = UIMetaUtils.mddMetaDaoHelp().getEntity(fullName);
        //检查是否有编码字段
        String codeField = entity.codeAttribute() != null ? entity.codeAttribute().name() : "";
        if (StringUtils.isEmpty(codeField)) {
            return true;
        }
        List<BizObject> bills = getBizObjects(uiMetaBaseInfo, ruleContext);
        if (CollectionUtils.isEmpty(bills)) {
            return true;
        }

        for (BizObject bill : bills) {
            processMobileBillCode(componentService, usedBillCode, uiMetaBaseInfo, fullName, billNum, tenantId, entity, codeField, bill);
        }

        this.syncBillCode(componentService, billNum, InvocationInfoProxy.getSysid(), tenantId, usedBillCode.toArray(new String[0]), true);
        return false;
    }

    private void processMobileBillCode(IBillCodeComponentService componentService, List<String> usedBillCode, UIMetaBaseInfo uiMetaBaseInfo,
                                       String fullName, String billNum, String tenantId, Entity entity, String codeField, BizObject bill) {
        if (bill.getEntityStatus().equals(EntityStatus.Unchanged) || !BizObjUtils.isAutoCode(fullName)) {//未改变或者非自动编码的跳过
            return;
        }
        BillCodeObj billCodeObj = this.getBillCodeObj(fullName, bill);
        String billcode = bill.get(codeField);
        BillCodeContext bcContext = componentService.getBillCodeContext(billNum, tenantId, InvocationInfoProxy.getSysid());
        if (StringUtils.isEmpty(billcode) || StringUtils.isBlank(billcode)) {
            if (bcContext.getBillnumMode().equals(0)) {//纯手动
                throw new MddRuleException("单据号不可为空");
            } else {//单据号为空，直接生成真号
                billcode = this.getBillCode(componentService, uiMetaBaseInfo, tenantId, codeField, bill, billCodeObj);
                usedBillCode.add(billcode);
            }
        } else {
            if (bcContext.getBillnumMode().equals(1) && bill.getEntityStatus().equals(EntityStatus.Insert)) { //自动编码，新增状态，直接生成真号
                billcode = this.getBillCode(componentService, uiMetaBaseInfo, tenantId, codeField, bill, billCodeObj);
                usedBillCode.add(billcode);
            }
        }
        if (this.isDuplicate(entity, codeField, fullName, bill, billcode)) {//判断重复
            usedBillCode.remove(bill.get(codeField).toString());
            if (bcContext.getBillnumMode().equals(0)) {//纯手工
                throw new MddRuleException("单据号重复");
            } else {
                retryGetBillCode(componentService, usedBillCode, uiMetaBaseInfo, fullName, tenantId, entity, codeField, bill, billCodeObj, 5);
            }
        } else {
            usedBillCode.add(billcode);

        }

    }


    /**
     * 生成单据号
     *
     * @return 单据号
     */
    private String getBillCode(IBillCodeComponentService componentService, UIMetaBaseInfo uiMetaBaseInfo, String tenantId, String codeField,
                               BizObject bill, BillCodeObj billCodeObj) {
        String billCode = componentService
                .getBillCode(findUnifyBillNum(uiMetaBaseInfo.getBillnum()), uiMetaBaseInfo.getFullname(), tenantId, InvocationInfoProxy.getSysid(),
                        billCodeObj, true);
        bill.set(codeField, billCode);
        return billCode;
    }

    /**
     * 尝试重新获取编码
     */
    private void retryGetBillCode(IBillCodeComponentService componentService, List<String> usedBillCode, UIMetaBaseInfo uiMetaBaseInfo,
                                  String fullName, String tenantId, Entity entity, String codeField, BizObject bill, BillCodeObj billCodeObj,
                                  int times) {
        String billcode;//重复获取5次
        int i = 1;
        while (i <= times) {
            billcode = this.getBillCode(componentService, uiMetaBaseInfo, tenantId, codeField, bill, billCodeObj);
            if (!this.isDuplicate(entity, codeField, fullName, bill, billcode)) {//判断重复
                usedBillCode.add(billcode);
                break;
            } else {
                i++;
            }
        }
        if (i > times) {
            throw new MddRuleException("单据号重复");
        }
    }

    /**
     * 构造生成编码需要的数据
     *
     * @param fullName 元数据
     * @param bill     表单数据
     */
    private BillCodeObj getBillCodeObj(String fullName, BizObject bill) {
        String jsonString = JSON.toJSONString(bill);
        String jsonStr = jsonString.replaceAll("\"\\$ref\":\".*?\"", "");
        jsonStr = jsonStr.replaceAll("\"\\_parent\":\".*?\"", "");
        BillCodeObj bcObj = new BillCodeObj();
        bcObj.setBillObjStr(jsonStr);

        return bcObj;
    }

    /**
     * 校验是否重号
     *
     * @param key
     * @param bill
     * @return
     */
    private boolean isDuplicate(Entity entity, String key, String fullName, BizObject bill, String billCode) {
        IBillCodeValidate billCodeValidate = null;
        try {
            billCodeValidate = UIMetaSDKContext.getBean(IBillCodeValidate.class);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        try {
            if (billCodeValidate != null) { //如果存在自定义校验
                log.info("编码规则--使用自定义验重,id:[{}],status:[{}],billcode:[{}]", bill.getId(), bill.getEntityStatus(), billCode);
                Map<String, Object> extendInfo = Maps.newHashMap();
                extendInfo.put("id", bill.getId());
                extendInfo.put("status", bill.getEntityStatus());
                return billCodeValidate.isDuplicate(fullName, key, billCode, extendInfo);
            } else {
                log.info("编码规则--使用系统默认验重");
                return this.isDuplicate(entity, key, fullName, bill);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return true;
    }

    /**
     * 校验是否重复
     *
     * @param entity
     * @param key
     * @param fullName
     * @param bill
     * @return
     */
    private boolean isDuplicate(Entity entity, String key, String fullName, BizObject bill) {
        try {
            PartitionContext contextData = PartitionUtils.getPartitionContextData(entity, true);
            contextData.put("store", "*");
            UniqueCheckWalker uniqueCheckWalker = new UniqueCheckWalker(contextData);
            Property attribute = MetaUtils.findAttribute(fullName, key);
            if (bill.getEntityStatus() == null) {
                if (bill.getId() == null) {
                    bill.setEntityStatus(EntityStatus.Insert);
                } else {
                    bill.setEntityStatus(EntityStatus.Update);
                }
            }
            return uniqueCheckWalker.uniqueCheck(entity, attribute, bill);
        } catch (MddBaseException e) {
            log.error("isDuplicate[{}]", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("isDuplicate[{}]", (String) bill.get(key));
            return true;
        }
    }

    /**
     * 与整体事务同步
     */
    private void syncBillCode(IBillCodeComponentService componentService, String billnum, String sysId, String tenantId, String[] billCodes,
                              boolean autoReturnBill) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            RetryTemplate retryTemplate = new RetryTemplate();
            SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
            simpleRetryPolicy.setMaxAttempts(3);/// 重试三次
            retryTemplate.setRetryPolicy(simpleRetryPolicy);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {///当事务正常提交时，commit所有的billcode
                    retryTemplate.execute((RetryCallback<Boolean, BillCodeException>) context -> {
                        log.info("生成单号[{}]", billCodes);
                        componentService.commitBatchBillCodes(billnum, billCodes, tenantId, sysId);
                        return true;
                    }, context -> {
                        log.error("retry commit {} count,fail", context.getRetryCount());
                        return false;
                    });
                }

                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {////当事务回滚时，commit已经使用过的billcode
                        if (autoReturnBill) {
                            log.info("回滚单号[{}]", billCodes);
                            retryTemplate.execute((RetryCallback<Boolean, BillCodeException>) context -> {
                                componentService.rollbackBatchBillCodes(billnum, billCodes, tenantId, sysId);
                                return true;
                            }, context -> {
                                log.error("retry rollback {} count,fail", context.getRetryCount());
                                return false;
                            });
                        }
                    }
                }
            });
        }
    }
}
