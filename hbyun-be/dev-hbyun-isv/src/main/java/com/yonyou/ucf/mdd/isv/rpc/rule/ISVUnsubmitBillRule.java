package com.yonyou.ucf.mdd.isv.rpc.rule;

import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.ext.bill.dto.BillDataDto;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.isv.service.ISVProcessService;
import com.yonyou.ucf.mdd.rule.utils.RuleUtil;
import org.imeta.orm.base.BizObject;
import org.springframework.beans.factory.annotation.Autowired;
import yonyou.bpm.rest.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class ISVUnsubmitBillRule extends AbstractCommonRule {

    @Autowired
    private ISVProcessService processService;

    public ISVUnsubmitBillRule() {
    }

    public RuleExecuteResult execute(BillContext billContext, Map<String, Object> paramMap) throws Exception {
        List<BizObject> bizObjectList = getBizObjects(billContext, paramMap);
        if(billContext.isSupportBpm()) {
            if(StringUtils.isNotBlank(billContext.getCardKey())
                    && !billContext.getCardKey().equals(billContext.getBillnum())){
                billContext.setBillnum(billContext.getCardKey());
            }
            processService.withdraw(billContext, bizObjectList);
        }
        return new RuleExecuteResult();
    }


    public <T extends BizObject> List<T> getBizObjects(BillContext ruleContext, Map<String, Object> paramMap) throws Exception {
        BillDataDto obj = (BillDataDto) paramMap.get(MddConstants.PARAM_PARAM);
        String fullname = ruleContext.getFullname();
        return RuleUtil.decodeBizObjects(fullname, obj);
    }
}
