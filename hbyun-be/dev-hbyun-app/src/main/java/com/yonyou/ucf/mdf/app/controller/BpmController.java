package com.yonyou.ucf.mdf.app.controller;

import com.google.gson.JsonObject;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.metadata.unified.context.UnifiedMDContextHolder;
import com.yonyou.ucf.mdd.bpm.model.BpmRequestBody;
import com.yonyou.ucf.mdd.bpm.model.BpmResponse;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.exceptions.ExceptionSubCode;
import com.yonyou.ucf.mdd.common.exceptions.MddMsgException;
import com.yonyou.ucf.mdd.isv.service.ISVProcessService;
import com.yonyou.ucf.mdf.app.common.ResultMessage;
import com.yonyou.ucf.mdf.app.util.ApplicationContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/bpm")
public class BpmController extends BaseController {

    private final ISVProcessService processService;

    private static final String TOKEN_KEY = "token";

    @RequestMapping(value = "/testConn", method = RequestMethod.POST)
    public void testConnect(HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "ok");
        jsonObject.addProperty("msg", "Connected!!!");
        renderJson(response, jsonObject.toString());
    }

    @RequestMapping(value = "/complete", method = RequestMethod.POST)
    public void complete(@RequestBody BpmRequestBody params, HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("BPM 终审回调 开始！！");
            String token = request.getHeader(TOKEN_KEY);
            String confToken = processService.getCallbackToken();
            log.info("BPM 终审回调 token : " + token);
            log.debug("BPM 终审回调 配置token : " + confToken);
            String res = BpmResponse.success();
            boolean exeContinue = true;
            if (!processService.isCheckToken()) {
                log.info("BPM 终审回调 未配置 token 验证。");
            } else {
                if (StringUtils.isBlank(confToken) || !confToken.equals(token)) {//token 验证
                    log.info("BPM 终审回调 token 验证失败!! return fail");
                    res = BpmResponse.fail("token 验证失败!! ");
                    exeContinue = false;
                } else {
                    log.info("BPM 终审回调 token 验证成功。");
                }
            }
            // 因为没有走拦截器，强制设置用户租户信息
            if (null != params.getTenantId()) {
                String yhtTenantId = params.getTenantId().split("_")[0];
                UnifiedMDContextHolder.setTenantId(yhtTenantId);

                MddBaseContext.setTenantId(yhtTenantId);
                MddBaseContext.setToken(params.getYht_access_token());

                InvocationInfoProxy.setTenantid(yhtTenantId);
                InvocationInfoProxy.setUserid(params.getUserId());

                ApplicationContextUtil.setThreadContext("tenantId", yhtTenantId);
                ApplicationContextUtil.setThreadContext("userId", params.getUserId());

            }
            if (exeContinue) {
                res = processService.complete(params);
            }
            log.info("BPM 终审回调 返回结果 {}", res);
            renderJson(response, res);
        } catch (Exception e) {
            log.error("complete失败", e);
            renderJson(response, BpmResponse.fail(e.getMessage()));
        }
    }

    /**
     * 注册 终审回调地址
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/registerInterface", method = RequestMethod.POST)
    public void registerInterface(@RequestBody BpmRequestBody params, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(params.getUserId())) {
                throw new MddMsgException("没有用户信息", ExceptionSubCode.PARAM_IS_NULL, new Object[]{"userid"});
            }
            if (StringUtils.isBlank(params.getTenantId())) {
                throw new MddMsgException("没有租户信息", ExceptionSubCode.PARAM_IS_NULL, new Object[]{"tenantid"});
            }

            String result = processService.registerInterface(params.getUserId(), params.getTenantId(), null);
            renderJson(response, ResultMessage.success(result));
        } catch (Exception e) {
            renderJson(response, ResultMessage.error(e.getMessage()));
            log.error("初始化失败", e);
        }

    }
}
