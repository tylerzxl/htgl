package com.yonyou.ucf.mdf.app.mobile.module.rule;

import com.yonyou.iuap.billcode.common.utils.SpringContextUtil;
import com.yonyou.uap.billcode.service.IBillCodeComponentService;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.core.utils.BizObjUtils;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.imeta.core.model.Entity;
import org.imeta.orm.base.BizObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/26 11:20 上午
 */
@Slf4j
@Primary
@NoArgsConstructor
@Component("mobileMddReturnBillCodeRule")
public class MobileMddReturnBillCodeRule extends AbstractMobileRule {

    @Override
    public <T> RuleExecuteResult execute(RuleContext ruleContext, T... tObjs) throws Exception {
        UIMetaBaseInfo uiMetaBaseInfo = ruleContext.getUiMetaBaseInfo();
        try {
            processReturnBillCodeForMobile(ruleContext, uiMetaBaseInfo);
        } catch (Exception e) {
            log.error("exception when do execute mobile billcode return rule", e);
        }
        return new RuleExecuteResult();
    }

    private void processReturnBillCodeForMobile(RuleContext ruleContext, UIMetaBaseInfo uiMetaBaseInfo) throws Exception {
        IBillCodeComponentService componentService = SpringContextUtil.getBean(IBillCodeComponentService.class);
        Entity entity = UIMetaUtils.mddMetaDaoHelp().getEntity(uiMetaBaseInfo.getFullname());
        //检查是否有编码字段
        String codeField = entity.codeAttribute() != null ? entity.codeAttribute().name() : "";
        String billnum = StringUtils.isBlank(uiMetaBaseInfo.getCardKey()) ? uiMetaBaseInfo.getBillnum() : uiMetaBaseInfo.getCardKey();
        if (StringUtils.isEmpty(codeField) || !BizObjUtils.isAutoCode(uiMetaBaseInfo.getFullname())) {
            return;
        }
        List<BizObject> bills = getBizObjects(uiMetaBaseInfo, ruleContext);
        String[] billcodes = bills.stream()
                .map(i -> i.get(codeField))
                .filter(i -> i != null && StringUtils.isNotBlank(i.toString()))
                .toArray(String[]::new);

        if (ArrayUtils.isEmpty(billcodes)) {
            log.debug("没有编码可以退号");
            return;
        }
        for (int i = 0; i < 3; i++) {
            try {
                componentService.returnBatchBillCodes(billnum, billcodes, ruleContext.getTenantId().toString(), "");
                break;
            } catch (Exception ex) {
                log.error("单据号退号报错", ex);
                if (i == 2) {
                    throw ex;
                }
            }
        }
    }
}
