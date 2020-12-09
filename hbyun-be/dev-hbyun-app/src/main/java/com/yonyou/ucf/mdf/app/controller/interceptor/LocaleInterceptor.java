package com.yonyou.ucf.mdf.app.controller.interceptor;


import com.yonyou.iuap.context.InvocationInfoProxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截处理 获取请求head 中的locale参数
 */
public class LocaleInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(InvocationInfoProxy.getLocale() != null) {
            return true;
        }
        String locale = request.getHeader("locale");
        if(StringUtils.isNotBlank(locale)) {
            InvocationInfoProxy.setLocale(locale);
        }
        return true;
    }

}
