package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.model.uimeta.BillEntity;
import com.yonyou.ucf.mdd.common.model.uimeta.BillItemRule;
import com.yonyou.ucf.mdd.common.model.uimeta.BillItemRuleScript;
import com.yonyou.ucf.mdd.uimeta.context.UIMetaSDKContext;
import com.yonyou.ucf.mdd.uimeta.itemrule.builder.JointQueryBuilder;
import com.yonyou.ucf.mdd.uimeta.itemrule.script.JavaScriptGenerator;
import com.yonyou.ucf.mdd.uimeta.itemrule.script.base.BaseScriptGenerator;
import com.yonyou.ucf.mdd.uimeta.itemrule.service.ItemRuleService;
import com.yonyou.ucf.mdd.uimeta.itemrule.service.JointQueryService;
import com.yonyou.ucf.mdd.uimeta.util.UIMetaUtils;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/itemrule")
public class ItemRuleController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(ItemRuleController.class);

	@RequestMapping("/script")
	public void getRuleScript(String billNumber, String language,
                              HttpServletRequest request, HttpServletResponse response){
		try{
			String script = null;

			if(StringUtils.isEmpty(script)){
				Collection<BillItemRuleScript> scripts = buildScript(billNumber);
				for(BillItemRuleScript scr : scripts){
					if(scr.getLanguage().equalsIgnoreCase(language)){
						script = scr.getScript();
						break;
					}
				}
			}
			renderJson(response, ResultMessage.data(script));
		}
		catch(Exception ex){
			logger.error("getRuleScript 失败：", ex);
			renderJson(response, ResultMessage.error(ex.getMessage()));
		}
	}
	
	private Collection<BillItemRuleScript> buildScript(String billNumber) throws Exception{
		//生成规则脚本
		List<BillItemRuleScript> scripts = new ArrayList<>();
		Object tenantId = UIMetaUtils.chkAndGetTenantId(CommonUtil.getTenantId(), billNumber, null, null, UIMetaUtils.TENANT_CHK_TYPE.BILL_BASE);
		//读取表单实体列表
		List<BillEntity> entities = UIMetaUtils.getBillEntities(billNumber, tenantId);
		//if(CollectionUtils.isEmpty(entities)) throw new MddBaseException(MddExceptionType.BASE_EXCEPTION,"没有定义实体");
        if(CollectionUtils.isEmpty(entities)) {
            return scripts; //查询为空直接返回
        }
		//生成规则脚本
		BaseScriptGenerator jsGenerator = new JavaScriptGenerator();
		//增加如果组会没有则使用0租户处理
		Map<String, BillItemRule> rules = ItemRuleService.getBillItemRules(billNumber, tenantId);
		//联查规则
		JointQueryService jointQuerySvc = (JointQueryService) UIMetaSDKContext.getBean(MddConstants.BEAN_JOINT_QUERY);
		Object jointQuery = JointQueryBuilder.buildQueryMap(
				jointQuerySvc.getQueryRuleMap(billNumber), entities);
		jsGenerator.getVariables().put("jointquery", jointQuery);
		jsGenerator.getVariables().put(MddConstants.PARAM_BILL_NUMB, billNumber);
		
		BillItemRuleScript script = jsGenerator.generateScript(
				billNumber, rules.values() , entities, 
				entities.get(0).getSubId(),MddConstants.STR_NUM_ONE);
		scripts.add(script);
		
		return scripts;
	}
	
	
	
	
}
