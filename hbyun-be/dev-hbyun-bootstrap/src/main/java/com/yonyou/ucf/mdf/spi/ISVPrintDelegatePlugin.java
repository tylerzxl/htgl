package com.yonyou.ucf.mdf.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.print.client.utils.PrintClientPropertyUtil;
import com.yonyou.iuap.security.rest.common.SignProp;
import com.yonyou.iuap.security.rest.factory.ClientSignFactory;
import com.yonyou.iuap.security.rest.utils.PostParamsHelper;
import com.yonyou.iuap.security.rest.utils.SignPropGenerator;
import com.yonyou.iuap.utils.PropertyUtil;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.domain.AccessTokenBO;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.service.TokenService;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.CommonAuthApi;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.pojo.TokenResult;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.print.PrintImpl;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ISVPrintDelegatePlugin {
  private final PrintImpl printImpl;
  private final CommonAuthApi authApi;
  private final TokenService tokenService;

  public ISVPrintDelegatePlugin(
      PrintImpl printImpl,
      CommonAuthApi authApi,
      TokenService tokenService) {
    this.printImpl = printImpl;
    this.authApi = authApi;
    this.tokenService = tokenService;
  }



  public String getCookie(HttpServletRequest request,String tenantId,String userId) {
    StringBuilder cookieStr = new StringBuilder();
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
      for (Cookie cookie : cookies) {
        if (cookie.getName().contains("isv_access_token")){
          AccessTokenBO accessTokenBO = tokenService.validate(cookie.getValue());
          TokenResult tempToken = authApi.generateToken(accessTokenBO.getTenantId(), accessTokenBO.getUserId());
          String yhtAccessToken = tempToken.getToken();
          cookieStr.append("yht_access_token").append("=").append(yhtAccessToken).append(";");
          InvocationInfoProxy.setToken(yhtAccessToken);
        }
        if ("wb_at".equalsIgnoreCase(cookie.getName())){
          cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }
      }
    return cookieStr.toString();
  }

  public String doGet(String uri, Map<String, String> params, String tenantId, String userId) {
    if (StringUtils.isEmpty(uri) || params == null){
      throw new RuntimeException("参数不合法：请检查uri和params");
    }
    if (tenantId == null){
      tenantId = "";
    }
    String serverName = PropertyUtil.getPropertyByKey("print.server.name");
    String url = serverName + "/iuap-print" + uri;
    Map<String, String> headers = generateHeaders(tenantId, userId, url);
    generateParamMap(params, tenantId);
    Map<String, Object> map = new HashMap<>();
    map.put("url",url);
    map.put("params", params);
    map.put("headers", headers);
    map.put("encode","UTF-8");
    String result = printImpl.setPost(map,tenantId);
    JSONObject jsonObject = JSON.parseObject(result);
    String jsonString = jsonObject.getString("data");
    return jsonString;
  }

  private static Map<String, String> generateHeaders(String tenantId, String userId, String url) {
    Map<String, String> headers = new HashMap<>();
    String credential = PropertyUtil.getPropertyByKey("print.client.credential.path");
    PrintClientPropertyUtil.setInnerPropertyName(credential);
    String appCode = PrintClientPropertyUtil.getInnerPropertyByKey("appCode");
    String appId = PrintClientPropertyUtil.getInnerPropertyByKey("appId");
    if (StringUtils.isEmpty(appCode) || StringUtils.isEmpty(appId))
      throw new RuntimeException("appCode参数不合法：请检查证书配置文件");
    headers.put("appCode", appCode);
    headers.put("tenantId", tenantId);
    headers.put("userId", userId);
    headers.put("appId", appId);
    headers.put("sign", generateSign(url, appCode, tenantId, appId));
    headers.put("ts", String.valueOf(System.currentTimeMillis()));
    String domainCode = (InvocationInfoProxy.getExtendAttribute("domainDataBaseByCode") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("domainDataBaseByCode");
    String domainName = (InvocationInfoProxy.getExtendAttribute("domainDataBaseByName") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("domainDataBaseByName");
    String applicCode = (InvocationInfoProxy.getExtendAttribute("currentMainClassCode") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("currentMainClassCode");
    String applicName = (InvocationInfoProxy.getExtendAttribute("currentMainClassName") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("currentMainClassName");
    String cookieStr = (InvocationInfoProxy.getExtendAttribute("cookie") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("cookie");
    String clientIp = (InvocationInfoProxy.getExtendAttribute("clientIp") == null) ? null : (String)InvocationInfoProxy.getExtendAttribute("clientIp");
    headers.put("domainDataBaseByCode", domainCode);
    headers.put("domainDataBaseByName", domainName);
    headers.put("currentMainClassCode", applicCode);
    headers.put("currentMainClassName", applicName);
    headers.put("cookie", cookieStr);
    headers.put("clientIp", clientIp);
    headers.put("specifiedLocale", (String)InvocationInfoProxy.getExtendAttribute("specifiedLocale"));
    return headers;
  }

  public static String generateSign(String url, String appCode, String tenantId, String appId) {
    Map<String, String> paramsMap = new HashMap<>();
    paramsMap.put("appCode", appCode);
    paramsMap.put("tenantId", tenantId);
    paramsMap.put("appId", appId);
    String sign = "";
    try {
      PrintClientPropertyUtil.setPrintAuth("print");
      SignProp signProp = SignPropGenerator.genSignProp(url);
      signProp.setPostParamsStr(PostParamsHelper.genParamsStrByMap(paramsMap));
      sign = ClientSignFactory.getSigner("print").sign(signProp);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("客户端签名识别", e);
    }
    return sign;
  }

  private static Map<String, String> generateParamMap(Map<String, String> params, String tenantId) {
    params.put("tenantId", tenantId);
    String lang = InvocationInfoProxy.getLocale();
    if (StringUtils.isNotEmpty(lang))
      params.put("lang", lang);
    return params;
  }

}
