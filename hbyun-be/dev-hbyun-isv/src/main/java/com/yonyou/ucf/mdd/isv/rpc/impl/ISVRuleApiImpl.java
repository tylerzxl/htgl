package com.yonyou.ucf.mdd.isv.rpc.impl;

import com.yonyou.ucf.mdd.api.interfaces.rpc.IRuleApi;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.rule.RuleRegister;

import java.util.List;
import java.util.Map;

/**
 * <p>Title</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-05-06$ 14:43$
 * @Version 1.0
 **/
public class ISVRuleApiImpl implements IRuleApi {

    @Override
    public <T> RuleExecuteResult executeRule(RuleRegister bizRule, RuleContext ruleContext, T... tObjs) {
        return null;
    }

    @Override
    public <T> RuleExecuteResult executeRule(RuleContext ruleContext) {
        return null;
    }

    @Override
    public RuleExecuteResult pureExecuteRule(RuleContext ruleContext, BaseReqDto bill) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getMakeBillRuleList(String targetType) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getRuleBackSourceList(List<String> makeBillCode) {
        return null;
    }

    @Override
    public RuleExecuteResult doAction(String action, RuleRegister ruleRegister, RuleContext ruleContext) {
        return null;
    }

}
