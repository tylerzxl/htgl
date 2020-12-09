package com.yonyou.ucf.mdf.app.service.impl;

import com.yonyou.ucf.mdd.api.interfaces.rpc.IRuleApi;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.exceptions.MddRuleException;
import com.yonyou.ucf.mdd.common.interfaces.rule.IExecRulesHandler;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.rule.RuleRegister;
import com.yonyou.ucf.mdd.ext.bill.rule.base.IRule;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.rule.api.RuleEngine;
import com.yonyou.ucf.mdd.rule.handler.DefaultExecRulesHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title:
 * Description: TODO
 *
 * @author fuxhh
 * @Date: 2020/2/14 15:35
 * @Version 1.0
 */
@Slf4j
@Service("mddRuleApiService")
public class MddRuleApiServiceImpl implements IRuleApi {
    @Override
    public <T> RuleExecuteResult executeRule(RuleRegister bizRule, RuleContext ruleContext, T... tObjs) throws Exception {
        IExecRulesHandler execRulesHandler = ruleContext.getExecRulesHandler();
        if (null == execRulesHandler) {
            execRulesHandler = new DefaultExecRulesHandler();
        }
        IRule rule = execRulesHandler.getRule(bizRule);
        if (null != rule) {
            RuleExecuteResult result = rule.execute((BillContext) ruleContext.getBillContext(), ruleContext.getCustomMap());
            Map<String, Object> outParams = new HashMap();
            outParams.put("ruleContext", ruleContext);
            result.setOutParams(outParams);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public <T> RuleExecuteResult executeRule(RuleContext ruleContext) throws Exception {
        try {
            RuleExecuteResult result = RuleEngine.getInstance().execute(ruleContext);
            return result;
        } catch (Exception e) {
            log.error("executeRule Exception", e);
            throw new MddRuleException(e.getMessage(),e);
        }
    }

    @Override
    public RuleExecuteResult pureExecuteRule(RuleContext ruleContext, BaseReqDto bill) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> getMakeBillRuleList(String targetType) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> getRuleBackSourceList(List<String> makeBillCode) throws Exception {
        return null;
    }

    @Override
    public RuleExecuteResult doAction(String action, RuleRegister ruleRegister, RuleContext ruleContext) throws Exception {
        return null;
    }
}
