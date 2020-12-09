package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.uimeta.dto.BillStateRuleDTO;
import com.yonyou.ucf.mdd.uimeta.itemrule.service.RuleExpressionService;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/billstaterule")
public class BillStateRuleController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(BillStateRuleController.class);


    @RequestMapping(value = {"/staterules/{billno}/{templateid}"}, method = RequestMethod.GET)
    public void getRuleExpression(@PathVariable("billno") String billno, @PathVariable("templateid") String templateid, HttpServletRequest request, HttpServletResponse response) {
        try {
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            List ruleExpressionList = ruleExpressionService.listRuleExpression(billno, templateid);
            renderJson(response, ResultMessage.data(ruleExpressionList));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }


    @RequestMapping("/staterules")
    public void getRuleExpression2(String billno, String templateid, HttpServletRequest request, HttpServletResponse response) {
        try {
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            List ruleExpressionList = ruleExpressionService.listRuleExpression(billno, templateid);
            renderJson(response, ResultMessage.data(ruleExpressionList));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }


    @RequestMapping(value = "/staterules", method = RequestMethod.PUT)//保持和 原前端请求路径一致
    public void addStateRule(@RequestBody BillStateRuleDTO billRuleDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            billRuleDTO.setTenantId(CommonUtil.getTenantId());

            BillStateRuleDTO[] list = {billRuleDTO};
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            ruleExpressionService.add(list);
            renderJson(response, ResultMessage.data(true));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }

    @RequestMapping(value = "/staterules/{ruleid}", method = RequestMethod.DELETE)//保持和 原前端请求路径一致
    public void deleteStateRule(@PathVariable("ruleid") String id, String billno, String templateid, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty(id) || StringUtils.isBlank(id)) {
                renderJson(response, ResultMessage.data(true));
            }

            BillStateRuleDTO billRuleDTO = new BillStateRuleDTO();
            billRuleDTO.setId(id);
            billRuleDTO.setBillNo(billno);
            billRuleDTO.setTemplateId(templateid);

            //无视客户端传入的tenantId，否则可能会造成客户端恶意修改其他租户的数据
            billRuleDTO.setTenantId(CommonUtil.getTenantId());

            BillStateRuleDTO[] array = {billRuleDTO};
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            ruleExpressionService.deleteStateRule(array);
            renderJson(response, ResultMessage.data(true));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }

    @RequestMapping(value = "/staterules/conditions", method = RequestMethod.DELETE)//保持和 原前端请求路径一致
    public void deleteAction(@RequestBody BillStateRuleDTO billRuleDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isEmpty(billRuleDTO.getId()) || StringUtils.isBlank(billRuleDTO.getId())) {
                renderJson(response, ResultMessage.data(true));
            }
            //无视客户端传入的tenantId，否则可能会造成客户端恶意修改其他租户的数据
            billRuleDTO.setTenantId(CommonUtil.getTenantId());

            BillStateRuleDTO[] list = {billRuleDTO};
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            ruleExpressionService.deleteAction(list);


            renderJson(response, ResultMessage.data(true));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }

//    @RequestMapping(value = "/staterules/actions/{ruleid}/{actid}", method = RequestMethod.DELETE)//保持和 原前端请求路径一致
//    public void deleteAction(@RequestBody BillStateRuleDTO billRuleDTO, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            if (StringUtils.isEmpty(billRuleDTO.getId()) || StringUtils.isBlank(billRuleDTO.getId())) {
//                renderJson(response, ResultMessage.data(true));
//            }
//            //无视客户端传入的tenantId，否则可能会造成客户端恶意修改其他租户的数据
//            billRuleDTO.setTenantId(CommonUtil.getTenantId());
//
//            BillStateRuleDTO[] list = {billRuleDTO};
//            RuleExpressionService ruleExpressionService = new RuleExpressionService();
//            ruleExpressionService.deleteAction(list);
//
//
//            renderJson(response, ResultMessage.data(true));
//        } catch (Exception e) {
//            e.printStackTrace();
//            renderJson(response, ResultMessage.error(e.toString()));
//        }
//    }

    @RequestMapping(value = "/staterules", method = RequestMethod.PATCH)//保持和 原前端请求路径一致
    public void updateStateRule(@RequestBody BillStateRuleDTO stateRuleDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            //无视客户端传入的tenantId，否则可能会造成客户端恶意修改其他租户的数据
            stateRuleDTO.setTenantId(CommonUtil.getTenantId());

            BillStateRuleDTO[] list = {};
            RuleExpressionService ruleExpressionService = new RuleExpressionService();
            ruleExpressionService.update(list);


            renderJson(response, ResultMessage.data(true));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(response, ResultMessage.error(e.toString()));
        }
    }


}
