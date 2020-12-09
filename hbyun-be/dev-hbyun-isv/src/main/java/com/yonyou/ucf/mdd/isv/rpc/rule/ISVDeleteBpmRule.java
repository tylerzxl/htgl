package com.yonyou.ucf.mdd.isv.rpc.rule;

import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.bill.rule.util.AuditFlowUtils;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.ext.util.Toolkit;
import lombok.RequiredArgsConstructor;
import org.imeta.orm.base.BizObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *  撤回审批流规则：删除流程实例
 * @author Bob
 *
 */
@RequiredArgsConstructor
@Component("deleteBpmRule")
public class ISVDeleteBpmRule extends AbstractCommonRule {

    @Override
    public RuleExecuteResult execute(BillContext billContext, Map<String,Object> paramMap) throws Exception {
        List<BizObject> bills=getBills(billContext,paramMap);
        if(billContext.isSupportBpm()) {
            if(!Toolkit.isEmpty(billContext.getCardKey())){
                if(!billContext.getCardKey().equals(billContext.getBillnum())){
                    billContext.setBillnum(billContext.getCardKey());
                }
            }
            if(null!=billContext.getDeleteReason()){
                if(!AuditFlowUtils.withdraw(billContext, bills)){
                    RuleExecuteResult result=new RuleExecuteResult();
                    //result.isCancel=true;
                    return result;
                }else{
                    //putParam(paramMap, bills);
                    return new RuleExecuteResult();
                }
            }/*else{
				processService.withdraw(billContext, bills);
			}*/
        }
        //putParam(paramMap, bills);
        return new RuleExecuteResult();
    }

}
