package com.yonyou.ucf.mdf.app.extend.rule;

import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("ISVDemoRuleOne")
public class ISVDemoRuleOne extends AbstractCommonRule {

  public ISVDemoRuleOne(){

  }

  @Override
  public RuleExecuteResult execute(BillContext billContext, Map<String, Object> paramMap)
      throws Exception {
    paramMap.put("ISVDemoRuleOne","ISVDemoRuleOne传递给ISVDemoRuleTwo的信息");
    return null;
  }
}
