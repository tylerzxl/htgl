package com.yonyou.ucf.mdd.isv.sso.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>访问令牌</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-03-31$ 13:39$
 * @Version 1.0
 **/
@Getter
@Setter
public class AccessTokenResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 获取的访问令牌 access_token
     */
    @JsonProperty(value = "access_token")
    private String accessToken;

    /**
     * 访问令牌的过期时间，单位秒
     */
    private int expire;

}
