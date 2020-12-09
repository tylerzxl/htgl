package com.yonyou.ucf.mdd.isv.rpc.rule;

import com.yonyou.iuap.utils.PropertyUtil;
import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.ext.bill.dto.BillDataDto;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.exceptions.BusinessException;
import com.yonyou.ucf.mdd.ext.i18n.utils.MddMultilingualUtil;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.isv.service.ISVProcessService;
import com.yonyou.ucf.mdd.rule.utils.RuleUtil;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import org.imeta.orm.base.BizObject;
import org.springframework.beans.factory.annotation.Autowired;
import yonyou.bpm.rest.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ISVSubmitBillRule extends AbstractCommonRule {

    @Autowired
    private ISVProcessService processService;

    public ISVSubmitBillRule() {
    }

    public RuleExecuteResult execute(BillContext billContext, Map<String, Object> paramMap) throws Exception {
        List<BizObject> bizObjectList = getBizObjects(billContext, paramMap);
        if (billContext.isSupportBpm()) {
            String appSource = PropertyUtil.getPropertyByKey("bpmrest.appsource");
            String cardKey = billContext.getCardKey();
            if (StringUtils.isNotBlank(cardKey) && !cardKey.equals(billContext.getBillnum())) {
                List<BizObject> newBills = new ArrayList<>();
                for (BizObject bill : bizObjectList) {

                    String billTypeId = bill.get("billTypeId");
                    // mdd-bpm中补充单据类型 --yanx于2020/6/16注释
                    if (null == billTypeId) {
                        billTypeId = cardKey;
                        if (StringUtils.isNotEmpty(appSource)) {
                            billTypeId = appSource + "." + billTypeId;
                        }
                    } else if (StringUtils.isNotBlank(billTypeId)) {
                        billTypeId = billTypeId.replace(billContext.getBillnum(), billContext.getCardKey());
                    }

                    Object id = bill.getId();
                    if (null != id) {
                        BizObject bizObject = UIMetaUtils.mddMetaDaoHelp().findById(billContext.getFullname(), id);
                        if (!bizObject.containsKey("billTypeId") && StringUtils.isNotBlank(billTypeId)) {
                            bizObject.put("billTypeId", billTypeId);
                        }
                        newBills.add(bizObject);
                    }
                }

                if (newBills.size() > 0) {
                    bizObjectList.clear();
                    bizObjectList.addAll(newBills);
                }
                billContext.setBillnum(billContext.getCardKey());
            } else {
                for (BizObject bill : bizObjectList) {
                    String billTypeId = bill.get("billTypeId");
                    // mdd-bpm中补充单据类型 --yanx于2020/6/16注释
                    if (null == billTypeId) {
                        billTypeId = billContext.getBillnum();
                        if (StringUtils.isNotEmpty(appSource)) {
                            billTypeId = appSource + "." + billTypeId;
                        }
                        bill.set("billTypeId", billTypeId);
                    }
                }
            }

            processService.startBpm(billContext, bizObjectList);
        }

        return new RuleExecuteResult();

    }

    public <T extends BizObject> List<T> getBizObjects( RuleContext ruleContext,Map<String, Object> paramMap) throws Exception {
        BillDataDto obj = (BillDataDto) paramMap.get(MddConstants.PARAM_PARAM);
        String fullname = ruleContext.getFullname();
        return RuleUtil.decodeBizObjects(fullname, obj);
    }


    private RuleExecuteResult finalAudit(BillContext billContext, List<BizObject> bills) throws Exception {
        throw new BusinessException(MddMultilingualUtil.getFWMessage("P_YS_FW-PUB_MDD-BACK_0001065433", "不支持审批流"));
    }
}
