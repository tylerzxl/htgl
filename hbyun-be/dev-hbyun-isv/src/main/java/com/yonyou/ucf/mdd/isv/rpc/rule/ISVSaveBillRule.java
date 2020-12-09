package com.yonyou.ucf.mdd.isv.rpc.rule;


import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.core.utils.BizObjUtils;
import com.yonyou.ucf.mdd.core.utils.ConditionUtil;
import com.yonyou.ucf.mdd.core.utils.DateKit;
import com.yonyou.ucf.mdd.core.utils.IMetaUtils;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.bill.rule.util.AuditFlowUtils;
import com.yonyou.ucf.mdd.ext.bill.rule.util.BillInfoUtils;
import com.yonyou.ucf.mdd.ext.bill.service.BillMasterOrgService;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.ext.dao.meta.MetaDaoHelper;
import com.yonyou.ucf.mdd.ext.exceptions.BusinessException;
import com.yonyou.ucf.mdd.ext.exceptions.ZeroResultException;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.ext.util.Toolkit;
import com.yonyou.ucf.mdd.ext.voucher.enums.Status;
import com.yonyou.ucf.mdd.isv.service.ISVProcessService;
import lombok.RequiredArgsConstructor;
import org.imeta.biz.base.BizContext;
import org.imeta.biz.base.FieldFormatException;
import org.imeta.biz.base.MetaUtils;
import org.imeta.core.lang.ArrayUtils;
import org.imeta.core.lang.BooleanUtils;
import org.imeta.core.model.DataType;
import org.imeta.core.model.Entity;
import org.imeta.core.model.Property;
import org.imeta.core.model.PropertyMap;
import org.imeta.orm.base.BizObject;
import org.imeta.orm.base.EntityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Component("enhanceSaveBillRule")
public class ISVSaveBillRule extends AbstractCommonRule {
    private static final Logger log = LoggerFactory.getLogger(com.yonyou.ucf.mdd.ext.bill.rule.crud.SaveBillRule.class);

    @Autowired
    private ISVProcessService processService;

    @Override
    public RuleExecuteResult execute(BillContext billContext, Map<String, Object> paramMap) throws Exception {

        List<BizObject> bills = getBills(billContext, paramMap);

        for (BizObject bill : bills) {
            boolean isWfControlled = false;
            if (billContext.isSupportBpm()) {
                isWfControlled = processService.bpmControl(billContext, bill);
            }
            boolean isInsert;
            if (EntityStatus.Insert == bill.getEntityStatus()) {//新增
                isInsert = true;
                BillInfoUtils.setAddAuditInfo(bill);
            } else {                  //修改
                isInsert = false;
                BillInfoUtils.setEditAuditInfo(bill);
            }

            //设置审计信息
            setAuditInfo(bill, AppContext.getUserId(), billContext.getFullname(), isInsert);

            setEnableInfo(bill, billContext.getFullname(), isInsert);

            if (isInsert) {
                BillInfoUtils.setStatusInfo(bill, Status.newopen);
            }

            if (billContext.isSupportBpm()) {
                bill.set("isWfControlled", isWfControlled);
                if (isWfControlled) {
                    if (isInsert) {
                        bill.set("verifystate", Short.valueOf("0"));
                    } else {
                        Integer verifystate = (Integer) AuditFlowUtils.getApprovalFlowData(billContext, bill, "verifystate");
                        if (null == verifystate) {
                            verifystate = 0;
                        }
                        if (1 == verifystate) {//已提交,更新流程变量
                            processService.updateBpmProcess(billContext, bill);
                        }
                        bill.set("verifystate", verifystate);
                    }
                }

                com.yonyou.ucf.mdd.ext.util.Logger.info(String.format("审批流支持日志： 主键: %s isWfControlled： %s", bill.getId(), bill.get("isWfControlled")));
            }

            String code = null;
            if (BillInfoUtils.isAutoCode(billContext.getFullname())) {
                code = bill.get("code");
                if (!Toolkit.isEmpty(code)) {
                    Property attr = MetaUtils.findAttribute(billContext.getFullname(), "code");
                    if (null != attr) {
                        DataType t = (DataType) attr.type();
                        if (t.isString()) {
                            if (attr.iLength() != null && code.toString().length() > attr.iLength().intValue()) {
                                throw new FieldFormatException(attr, "Length");
                            }
                        }
                    }
                }
                if (isInsert) {
                    if (null != code && null != bill.getId()) {
                        // bill.set("code", bill.getId());
                        //兼容自动编码不更新code逻辑
                        executeAutoCode(code,bill);
                    }
                } else {
                    bill.remove("code");
                }
            }

            try {
                Object oldParent = null;
                String oldPath = null;
                if (isInsert) {
                    // 递归处理主子孙的pubts字段
                    fillPubts4Insert(bill);
                    MetaDaoHelper.insert(billContext.getFullname(), bill);
                } else {
                    Map<String, Object> oldTree = BillInfoUtils.getOldParent(billContext.getFullname(), bill);
                    if (null != oldTree && oldTree.size() > 0) {
                        oldParent = oldTree.get("parent");
                        oldPath = (String) oldTree.get("path");
                    }
                    //更新主子表逻辑删除字段
                    Entity entity = BizContext.getMetaRepository().entity(billContext.getFullname());
                    handleLogicDelTag(entity, bill);
                    MetaDaoHelper.update(billContext.getFullname(), bill);
                    //BillInfoUtils.updatePathWhenUpdate(bill, billContext.getFullname());
                }
                if (null != code) {
                    bill.set("code", code);
                }
                BillInfoUtils.updateTree(bill, billContext.getFullname(), AppContext.getTenantId(), oldParent, oldPath);
            } catch (ZeroResultException zr) {
                throw new BusinessException(com.yonyou.ucf.mdd.ext.i18n.utils.MddMultilingualUtil.getFWMessage("P_YS_FW-PUB_MDD-BACK_0001065223", "当前单据不是最新状态，请刷新重试。") /* "当前单据不是最新状态，请刷新重试。" */);
            }
        }
        BillMasterOrgService.insertLastOrg(billContext.getBillnum());
        //putParam(paramMap, bills);
        if (null != bills && bills.size() > 0 && BillInfoUtils.isAutoCode(billContext.getFullname())) {
            putParam(paramMap, "refreshField", "code");
        }
        return new RuleExecuteResult();
    }


    /**
     * @param code
     * @param bill
     */
    protected void executeAutoCode(String code, BizObject bill) {
        com.yonyou.ucf.mdd.ext.util.Logger.debug("兼容Save规则前设置自动编码不更新Code逻辑");
    }

    /**
     * insert态递归处理主子表的pubts字段赋值
     *
     * @param bizObject
     */
    private void fillPubts4Insert(BizObject bizObject) {
        String tskey = "pubts";
        Object pubts = bizObject.get(tskey);
        if (null == pubts) {
            bizObject.set(tskey, new Date());
        }
        bizObject.forEach((key, value) -> {
            if (value instanceof BizObject) {
                EntityStatus entityStatus = ((BizObject) value).getEntityStatus();
                if (EntityStatus.Insert == entityStatus) {// 如果子表是insert态
                    if (null == ((BizObject) value).get(tskey)) {// 如果子表pubts为空
                        ((BizObject) value).set(tskey, new Date());
                    }
                }
            } else if (value instanceof List) {
                ((List) value).forEach(subvalue -> {
                    if (subvalue instanceof BizObject) {
                        fillPubts4Insert((BizObject) subvalue);
                    }
                });
            }
        });
    }

    /**
     * 更新主子表逻辑删除字段
     *
     * @param entity
     * @param data
     */
    private void handleLogicDelTag(Entity entity, BizObject data) {
        //self
        boolean isDeleted = ConditionUtil.isDeletable(entity);
        if (isDeleted && data.getEntityStatus() == EntityStatus.Delete) {
            data.setEntityStatus(EntityStatus.Update);
            if (!data.containsKey(ConditionUtil.getLogicDelName())) {
                data.set(ConditionUtil.getLogicDelName(), ConditionUtil.DELETED);
            }
        }

        PropertyMap attrMap = entity.attrMap();
        for (String key : data.persistenceSet()) {
            Property attr = attrMap.get(key);
            if (attr == null) {
                continue;
            }
            if (BooleanUtils.b(attr.isCompositionAttribute())
                    && BooleanUtils.b(attr.isRoleA())) {
                Entity child = (Entity) attr.type();
                Object obj = data.get(key);
                if (obj == null) {
                    continue;
                }
                if (obj instanceof BizObject) {
                    handleLogicDelTag(child, (BizObject) obj);
                } else if (obj instanceof Collection<?>) {
                    ((Collection<?>) obj).forEach(obj1 -> handleLogicDelTag(child, (BizObject) obj1));
                } else if (ArrayUtils.isArray(obj)) {
                    Arrays.stream((Object[]) obj).forEach(obj1 -> handleLogicDelTag(child, (BizObject) obj1));
                }
            }
        }
    }

    public void setEnableInfo(BizObject bill, String fullname, boolean isInsert) {
        boolean isItfEnable = IMetaUtils.isImplementOf(fullname, "ucfbase.ucfbaseItf.IEnable") || BizObjUtils.isImplementOf(fullname, "common.base.Enable");
        if (isItfEnable && isInsert) {
            bill.set("enable", 1);
            bill.set("enablets", DateKit.getNowDate());
        }
    }

    public void setAuditInfo(BizObject bizObject, Object userid, String fullname, boolean isInsert) {
        // 判断元数据是否继承审计信息接口  推荐的标准元数据接口为 basedoc.basedocItf.IAuditInfo
        boolean isAuditItf = IMetaUtils.isImplementOf(fullname, "ucfbase.ucfbaseItf.IAuditInfo");
        if (isAuditItf) {
            if (isInsert) {
                bizObject.set(MddConstants.PARAM_CREATOR, userid);
                bizObject.set("createTime", new Date());
            } else {
                bizObject.set(MddConstants.PARAM_MODIFIER, userid);
                bizObject.set("modifyTime", new Date());
            }
        }
    }
}