package com.yonyou.ucf.mdf.app.util;

import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.enums.OperationTypeEnum;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.core.utils.UIMetaHelper;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleEngineUtils {

    private static String appsource;

    @Value("${bpmrest.appsource}")// 租户应用 source 租户表
    private void setAppsource(String appsource) {
        RuleEngineUtils.appsource = appsource;
    }

    public static RuleContext prepareRuleContext(BaseReqDto queryParam, String action) throws Exception {
        RuleContext ruleContext = prepareRuleContext(queryParam);
        OperationTypeEnum operationTypeEnum = OperationTypeEnum.find(action);
        if (null == operationTypeEnum) {
            ruleContext.setOperationTypeEx(action);
        }
        ruleContext.setOperateType(operationTypeEnum);
        return ruleContext;
    }

    public static RuleContext prepareRuleContext(BaseReqDto queryParam, OperationTypeEnum operationTypeEnum) throws Exception {
        RuleContext ruleContext = prepareRuleContext(queryParam);
        ruleContext.setOperateType(operationTypeEnum);
        return ruleContext;
    }

    /**
     * 构造默认的param为key的custommap
     *
     * @param queryParam
     * @return
     * @throws Exception
     */
    public static RuleContext prepareRuleContext(BaseReqDto queryParam) throws Exception {
        RuleContext ruleContext = new RuleContext();
        String billnum = queryParam.getBillnum();
        Object tenantId = queryParam.getTenantId();
        if (null == tenantId || StringUtils.isBlank(tenantId.toString())) {
            tenantId = MddBaseContext.getTenantId();
        }
        UIMetaBaseInfo uiMetaBaseInfo = null;
        if (null == billnum) {
            //if (!(queryParam instanceof BaseReqDto)) {
                uiMetaBaseInfo = new UIMetaBaseInfo();
            //} else {
            //    throw new MddMsgException("表单编码不能为空", ExceptionSubCode.PARAM_IS_NULL,new String[]{"billnum"});
            //}
        } else {
            uiMetaBaseInfo = UIMetaUtils.getUIMetaBaseInfo(billnum, tenantId);
            if(null == uiMetaBaseInfo || StringUtils.isBlank(uiMetaBaseInfo.getBillnum())){
                uiMetaBaseInfo = UIMetaHelper.getUIMetaBaseInfo(billnum, MddConstants.STR_NUM_ZERO);
            }
        }
        uiMetaBaseInfo.setI18ndoc(true);// 暂时强行设置启用单据多语 --yanx于2020/7/1注释
        uiMetaBaseInfo.setPartitonable(queryParam.isPartitionable());
        ruleContext.setTenantId(tenantId);
        ruleContext.setUserId(CommonUtil.getUserId());
        ruleContext.setBillContext(uiMetaBaseInfo);
        ruleContext.setUiMetaBaseInfo(uiMetaBaseInfo);
        // 增加打印多语的判断条件
        if(queryParam.isPrint()){
            uiMetaBaseInfo.setI18ndoc(false);
        }

        ruleContext.setParamObj(queryParam);
        prepareParams(ruleContext, null, queryParam);
        String[] ruleLvs = new String[3];
        ruleLvs[0] = "common";
        ruleLvs[1] = uiMetaBaseInfo.getSubid();
        ruleLvs[2] = uiMetaBaseInfo.getBillnum();
        ruleContext.setRuleLvs(ruleLvs);
        ruleContext.setCusMapValue(MddConstants.PARAM_PARAM, queryParam);
        // ruleContext.setRuleListHandler(new RemoteRuleListHandler());
        return ruleContext;
    }

    public static RuleContext prepareRuleContext(BaseReqDto bill, OperationTypeEnum refer, Map<String, Object> params) throws Exception {
        RuleContext ruleContext = prepareRuleContext(bill, refer);
        ruleContext.setCustomMap(params);
        return ruleContext;
    }

    private static void prepareParams(RuleContext ruleContext, String key, Object param) {
        if (StringUtils.isEmpty(key)) {
            key = "ref";
        }
        ruleContext.setCusMapValue(key, param);
    }

}
