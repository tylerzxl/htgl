package com.yonyou.ucf.mdf.app.mobile.module;

import com.yonyou.ucf.mdf.app.mobile.MobileProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/7/2 11:33 上午
 */
@Slf4j
@Service
public class MobileTemplateServiceImpl implements MobileTemplateService {

    private static final String PATH_MOBILE_EXISTS = "registered/isPageBillNo?billNo=%s&tenantId=%s";

    private final MobileProperties properties;
    private final RestTemplate restTemplate;

    public MobileTemplateServiceImpl(MobileProperties properties, ApplicationContext context) {
        this.properties = properties;
        restTemplate = new MobileRestTemplate(1000L, 3000L, 300, 100, context).getRestTemplate();
    }

    @Override
    public boolean exists(String billNum, String tenantId) {

        String appMenuListHost = properties.getAppMenuListHost();
        String split;
        if (appMenuListHost.endsWith("/")) {
            split = "";
        } else {
            split = "/";
        }
        String url = appMenuListHost + split + String.format(PATH_MOBILE_EXISTS, billNum, tenantId);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Response> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Response.class);
        } catch (Exception e) {
            log.error("Exception when query whether mobile template exists", e);
            return true;
        }
        Response body = responseEntity.getBody();
        if (body != null && body.getData() != null) {
            ResponseData data = body.getData();
            return data.getInfo() != null && data.getInfo();
        }
        return false;
    }


    @Data
    public static class Response {
        private Integer code;
        private String message;
        private ResponseData data;
    }

    @Data
    public static class ResponseData {
        private Boolean info;
    }

}
