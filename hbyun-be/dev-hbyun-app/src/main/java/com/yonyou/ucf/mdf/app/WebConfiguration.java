package com.yonyou.ucf.mdf.app;

import com.yonyou.iuap.print.client.servlet.PrintDelegateServlet;
import com.yonyou.ucf.mdf.app.controller.interceptor.PrintFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/10/17 0017 13:58
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfiguration {

    @Bean
    public FilterRegistrationBean<PrintFilter> printFilter() {
        FilterRegistrationBean<PrintFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PrintFilter());
        registrationBean.addUrlPatterns("/print/printdelegate");
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServletRegistrationBean<PrintDelegateServlet> printServlet() {
        return new ServletRegistrationBean<>(new PrintDelegateServlet(), "/print/printdelegate");
    }


}
