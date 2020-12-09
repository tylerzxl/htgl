package com.yonyou.ucf.mdf.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/10/17 0017 14:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ISVAuthService {

    private static final String ISV_ACCESS_TOKEN_TEMPLATE = "isv_access_token_%s";
    private static String ISV_ACCESS_TOKEN_NAME = "isv_access_token";

    public static final String YHT_ACCESS_TOKEN = "yht_access_token";
    public static final String USER_ID = "userId";
    public static final String TENANT_ID = "tenantid";
    public static final String TENANT_ID_CAMEL_CASE = "tenantId";

    private final ApplicationContext applicationContext;


    @PostConstruct
    public void init() {
        //根据应用生成token名称，防止不同的应用之前的token冲突
        ISV_ACCESS_TOKEN_NAME = String.format(ISV_ACCESS_TOKEN_TEMPLATE, applicationContext.getId());
        log.info("isv cookie token name {}", ISV_ACCESS_TOKEN_NAME);
    }

    public String getISVTokenName() {
        return ISV_ACCESS_TOKEN_NAME;
    }

}
