package com.yonyou.ucf.mdf.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.iuap.print.client.plugin.IDelegatePlugin;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ISVIDelegatePlugin implements IDelegatePlugin {

  @Override
  public String doGetBefore(HttpServletRequest request, HttpServletResponse response){
    String realurl = request.getParameter("realurl");
    if (realurl.contains("queryBoforprint")){
      try {
        return doGetOpenApi(request,response);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private String doGetOpenApi(HttpServletRequest request,HttpServletResponse response)
      throws IOException {
    String realurl = request.getParameter("realurl");
    String tenantId = getTenantId(request);
    String userId = getUserId(request);
    Map<String, String> params = new HashMap<>();
    for (String key : request.getParameterMap().keySet()){
      params.put(key,request.getParameter(key));
    }
    setDomainInfoInContext(request);
    setCookieInContext(request,tenantId,userId);
    setLocaleInContext(request);
    try {
      ISVPrintDelegatePlugin ISVPrintDelegatePlugin = AppContext.getBean(ISVPrintDelegatePlugin.class);
      String ret = ISVPrintDelegatePlugin.doGet(realurl, params, tenantId,userId);
      if ((realurl.contains("queryBycodewithBO") || realurl.contains("getTemplateByPk")) && StringUtils.isNotEmpty(ret)) {
        JSONObject json = JSON.parseObject(ret);
        JSONObject templateContent = json.getJSONObject("tempContent");
        String tenantName = (String)InvocationInfoProxy.getExtendAttribute("tenantName");
        templateContent.put("CompanyName", StringUtils.isEmpty(tenantName) ? "" : tenantName);
        try {
          String userName = InvocationInfoProxy.getUsername();
          templateContent.put("CurrentName", StringUtils.isEmpty(userName) ? "" : userName);
        } catch (NoSuchMethodError e) {
          log.error("获取用户名称异常，{}", e.getMessage());
        }
        ret = json.toJSONString();
      }
      return ret;
    } catch (Exception e) {
      log.error("PrintDelegateServlet doGetOpenApi异常", e);
      response.getWriter().append(realurl + ",request error");
    }
    return null;
  }

  private String getUserId(HttpServletRequest request) {
    String userId = InvocationInfoProxy.getUserid();
    if (StringUtils.isEmpty(userId)) {
      userId = request.getParameter("userId");
      if (StringUtils.isEmpty(userId))
        userId = request.getHeader("userId");
    }
    return StringUtils.isEmpty(userId) ? "" : userId;
  }

  private String getTenantId(HttpServletRequest request) {
    String tenantId = InvocationInfoProxy.getTenantid();
    if (StringUtils.isEmpty(tenantId)) {
      tenantId = request.getHeader("tenantId");
    }
    if (StringUtils.isEmpty(tenantId)) {
      tenantId = request.getParameter("tenantId");
    }
    return tenantId;
  }

  private void setDomainInfoInContext(HttpServletRequest request) {
    String domainCode = (String)InvocationInfoProxy.getExtendAttribute("domainDataBaseByCode");
    if (StringUtils.isBlank(domainCode)) {
      domainCode = request.getHeader("domainDataBaseByCode");
      if (StringUtils.isEmpty(domainCode))
        domainCode = request.getParameter("domainDataBaseByCode");
      InvocationInfoProxy.setExtendAttribute("domainDataBaseByCode", domainCode);
    }
    String domainName = request.getHeader("domainDataBaseByName");
    if (StringUtils.isEmpty(domainName))
      domainName = request.getParameter("domainDataBaseByName");
    InvocationInfoProxy.setExtendAttribute("domainDataBaseByName", domainName);
  }

  private void setCookieInContext(HttpServletRequest request,String tenantId,String userId) {
    ISVPrintDelegatePlugin ISVPrintDelegatePlugin = AppContext.getBean(ISVPrintDelegatePlugin.class);
    String cookie = ISVPrintDelegatePlugin.getCookie(request,tenantId,userId);
    if (StringUtils.isNotEmpty(cookie))
      InvocationInfoProxy.setExtendAttribute("cookie", cookie);
  }



  private void setLocaleInContext(HttpServletRequest request) {
    if (StringUtils.isEmpty(InvocationInfoProxy.getLocale()) || "null".equals(InvocationInfoProxy.getLocale())) {
      String lang = request.getParameter("lang");
      InvocationInfoProxy.setLocale((StringUtils.isEmpty(lang) || "null".equals(lang)) ? String.valueOf(
          Locale.CHINA) : lang);
    }
    InvocationInfoProxy.setExtendAttribute("enableLangList", request.getHeader("enableLangList"));
  }



}
