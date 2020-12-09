package com.yonyou.ucf.mdf.app.mobile.module.rule;

import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.core.utils.BizObjUtils;
import com.yonyou.ucf.mdd.ext.model.BillContext;
import com.yonyou.ucf.mdd.utils.BillInfoUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imeta.orm.base.BizObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/26 11:20 上午
 */
@Slf4j
@Primary
@NoArgsConstructor
@Component("mobileMddAddRule")
public class MobileMddAddRule extends AbstractMobileRule {

    private static final String BILL_CODE_KEY = "code";
    private static final String RETURN_KEY = "return";

    @Override
    public <T> RuleExecuteResult execute(RuleContext ruleContext, T... tObjs) throws Exception {
        BillContext uiMetaBaseInfo = ruleContext.getBillContext();
        if (uiMetaBaseInfo.getbMain() != null && !uiMetaBaseInfo.getbMain()) {
            return new RuleExecuteResult();
        } else {
            try {
                processBillCodeForMobile(ruleContext, uiMetaBaseInfo);
            } catch (Exception e) {
                log.error("exception when do execute mobile billcode update rule", e);
            }
            return new RuleExecuteResult();
        }
    }

    private boolean processBillCodeForMobile(RuleContext ruleContext, BillContext uiMetaBaseInfo) throws Exception {
        String fullName = uiMetaBaseInfo.getFullname();
        List<BizObject> bills = getBizObjects(ruleContext);
        BizObject bill = BizObjUtils.add(uiMetaBaseInfo, fullName, bills, ruleContext.getUserName());

        String billNum = uiMetaBaseInfo.getBillnum();

        if (!isMobileBillNum(billNum)) {
            return true;
        }
        BillInfoUtils.setAutoCode(bill, findUnifyBillNum(billNum), fullName);

        Map<String, Object> returnMap = (Map<String, Object>) ruleContext.getCusMapValue(RETURN_KEY);
        returnMap.put(BILL_CODE_KEY, bill.get(BILL_CODE_KEY));
        return false;
    }
}
