package com.yonyou.ucf.mdd.isv.rpc.rule;


import com.yonyou.ucf.mdd.api.interfaces.rpc.IRefApi;
import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.dto.BaseReqDto;
import com.yonyou.ucf.mdd.common.exceptions.MddRuleException;
import com.yonyou.ucf.mdd.common.interfaces.ref.IRefService;
import com.yonyou.ucf.mdd.common.model.ref.RefEntity;
import com.yonyou.ucf.mdd.common.model.ref.RefInfo;
import com.yonyou.ucf.mdd.common.model.rule.RuleContext;
import com.yonyou.ucf.mdd.common.model.rule.RuleExecuteResult;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyou.ucf.mdd.core.interfaces.rpc.RPCServiceAdapter;
import com.yonyou.ucf.mdd.rule.base.AbstractRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component("mddReferDataRule")
public class MddReferDataRule extends AbstractRule {
    @Autowired
    @Qualifier("mddRefService")
    private IRefService refService;
    @Autowired
    private RPCServiceAdapter rpcServiceAdapter;

    @Override
    public <T> RuleExecuteResult execute(RuleContext ruleContext, T... tObjs) throws Exception {

        UIMetaBaseInfo uiMetaBaseInfo = ruleContext.getUiMetaBaseInfo();
        BaseReqDto refDataReqParam = (BaseReqDto) ruleContext.getParamObj();
        Map<String, Object> refpara = new HashMap<>();
        refpara.put(MddConstants.PARAM_CONDITION, refDataReqParam.getCondition());
        refpara.put(MddConstants.PARAM_MAP_CONDITION, refDataReqParam.getMapCondition());
        refpara.put(MddConstants.PARAM_TREE_MAP_CONDITION, refDataReqParam.getTreeMapCondition());
        refpara.put(MddConstants.PARAM_TREE_CONDITION, refDataReqParam.getTreeCondition());
        refpara.put(MddConstants.PARAM_PAGE, refDataReqParam.getPage());
        refpara.put(MddConstants.PARAM_LIKE_VALUE, refDataReqParam.getLikeValue());
        refpara.put(MddConstants.PARAM_DATA, refDataReqParam.getData());
        refpara.put(MddConstants.PARAM_DATA_TYPE, refDataReqParam.getDataType());
        refpara.put(MddConstants.PARAM_TENANT_ID, refDataReqParam.getTenantId());
        refpara.put(MddConstants.PARAM_IS_DISTINCT, refDataReqParam.isDistinct());
        refpara.put(MddConstants.PARAM_QUERY_ORDERS, refDataReqParam.getQueryOrders());
        refpara.put(MddConstants.PARAM_TREE_QUERY_ORDERS, refDataReqParam.getTreeQueryOrders());
        refpara.put(MddConstants.PARAM_PATH, refDataReqParam.getPath());
        refpara.put(MddConstants.PARAM_ORG_ID, refDataReqParam.getOrgId());

        refpara.put(MddConstants.PARAM_PK_FIELD, uiMetaBaseInfo.getPkField());
        refpara.put(MddConstants.PARAM_PARENT_FIELD, uiMetaBaseInfo.getParentField());
        refpara.put(MddConstants.PARAM_EXTERNAL_DATA,refDataReqParam.getExternalData());
        refpara.put(MddConstants.PARAM_PART_PARAM, refDataReqParam.getPartParam());

//        ViewControlParams viewControlParams = UIMetaUtils.getViewControlParam(ruleContext);

        RefEntity refEntity = refDataReqParam.getRefEntity();
        if (null == refEntity) {
            throw new MddRuleException("参照元数据不存在");
        }
        RefInfo rev = null;
        if (null != refEntity.outDomain) {
            // ICommonQueryRPCService billService = DubboUtils.getDubboService(ICommonQueryRPCService.class, refEntity.outDomain, null);
            IRefApi billService = rpcServiceAdapter.getIRefApiProxy(refEntity.outDomain, null, null);
            if (null != billService) {
                rev = (RefInfo) billService.getRefData(uiMetaBaseInfo, refEntity, refpara);
            }
        } else {
            rev = refService.getRefData(uiMetaBaseInfo, refDataReqParam.getRefEntity(), refpara, ruleContext.getTenantId());
        }
        Object result = null;
        if (MddConstants.TYPE_REF_DATATYPE_TREE.equalsIgnoreCase(refDataReqParam.getDataType())) {
            result = rev.treeData;
        } else if (MddConstants.TYPE_REF_DATATYPE_GRID.equalsIgnoreCase(refDataReqParam.getDataType())) {
            result = rev.gridData;
        } else {
            result = rev;
        }
        ruleContext.setCusMapValue(MddConstants.PARAM_MAP_RETURN, result);
        return new RuleExecuteResult(result);

    }

}
