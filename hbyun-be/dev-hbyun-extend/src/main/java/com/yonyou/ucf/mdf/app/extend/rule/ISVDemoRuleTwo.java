package com.yonyou.ucf.mdf.app.extend.rule;

import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.ext.bill.rule.base.AbstractCommonRule;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("ISVDemoRuleTwo")
public class ISVDemoRuleTwo extends AbstractCommonRule {

  public ISVDemoRuleTwo(){

  }

  @Override
  public RuleExecuteResult execute(BillContext billContext, Map<String, Object> paramMap)
      throws Exception {
    String oneStr = (String) paramMap.get("ISVDemoRuleOne");
    String message = "ISVDemoRuleTwo从ISVDemoRuleOne获取到" + oneStr;
    throw new RuntimeException(message);
  }
}