package com.yonyou.ucf.mdf.app.controller.interceptor.pojo;

import com.yonyoucloud.iuap.ucf.mdd.starter.token.domain.AccessTokenBO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/10/17 0017 14:19
 */
@Data
public class AuthRequestBO {

    private String trustedTenantId;

    private String trustedUserId;

    private String cookieTenantId;

    private String cookieUserId;

    private AccessTokenBO token;

    public String getTenantId() {
        if (StringUtils.isNotBlank(trustedTenantId)) {
            return trustedTenantId;
        }
        return cookieTenantId;
    }

    public String getUserId() {
        if (StringUtils.isNotBlank(trustedUserId)) {
            return trustedUserId;
        }
        return cookieUserId;
    }

    public boolean trusted() {
        return trustedTenantId != null || trustedUserId != null;
    }

}
