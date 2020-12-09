package com.yonyou.ucf.mdf.app.controller.interceptor;

import com.yonyou.iuap.context.InvocationInfoProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class PrintFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        try {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            //	ITenantUserService iTenantUserService = applicationContext.getBean(ITenantUserService.class);

//			UserVO userVO = iTenantUserService.getUserByUserId(InvocationInfoProxy.getUserid());
//			InvocationInfoProxy.setUsername(userVO.getUserName());
            InvocationInfoProxy.setUsername("");
//			TenantVO tenantVO = iTenantUserService.getTenant(InvocationInfoProxy.getTenantid());
            InvocationInfoProxy.setExtendAttribute("tenantName", "");
//			InvocationInfoProxy.setExtendAttribute("tenantName", tenantVO.getTenantName());
        } catch (Exception e) {
            log.error("exception when do print filter", e);
        } finally {
            chain.doFilter(request, response);
        }
    }

}
