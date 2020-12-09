package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.isvrequest.ISVRequest;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.isvrequest.ISVRequestBody;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hpapaas-passport-be/hpaextcoderegister")
@RestController
@RequiredArgsConstructor
public class HpapaasPassportController {

  private final ISVRequest isvRequest;

  @Value("${hpapaas-passport-be.host}")
  private String apiHost;

  private static final String PATH = "/hpaextcoderegister/getHpaExtCodeRegister";

  public static final String YHT_ACCESS_TOKEN = "yht_access_token";

  @RequestMapping(value = "/getHpaExtCodeRegister" , method = RequestMethod.GET, produces = "application/javascript")
  @ResponseBody
  public String getHpaExtCodeRegister(HttpServletRequest request){
    String result = "";
    String url = new StringBuilder().append(apiHost).append(PATH).toString();
    ISVRequestBody isvRequestBody = new ISVRequestBody();
    isvRequestBody.setUrl(url);
    isvRequestBody.setRequestMethod("GET");
    Map<String,String> param = new HashMap<>();
    for (String key : request.getParameterMap().keySet()){
      param.put(key,request.getParameter(key));
    }
    isvRequestBody.setParams(param);
    Map<String,String> headers = new HashMap<>();
    AtomicBoolean hasYhtAccessToken = new AtomicBoolean(false);
    Cookie[] cookies = request.getCookies();
    StringBuilder cookieStr = new StringBuilder();
    Stream.of(cookies).forEach(cookie -> {
      if (cookie.getName().equals(YHT_ACCESS_TOKEN)){
        hasYhtAccessToken.set(true);
      }
      cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
    });
    if (!hasYhtAccessToken.get()){
      cookieStr.append(YHT_ACCESS_TOKEN).append("=").append(InvocationInfoProxy.getExtendAttribute(YHT_ACCESS_TOKEN).toString()).append(";");
    }
    headers.put("Cookie",cookieStr.toString());
    isvRequestBody.setHeaders(headers);
    result = isvRequest.doRequest(InvocationInfoProxy.getTenantid(),isvRequestBody);
    return result;

  }

}
