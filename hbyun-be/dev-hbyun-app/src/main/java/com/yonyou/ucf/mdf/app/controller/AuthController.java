package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.isv.util.WebUtils;
import com.yonyou.ucf.mdf.app.service.impl.ISVAuthService;
import com.yonyoucloud.iuap.ucf.mdd.error.UCFAuthenticationFailedException;
import com.yonyoucloud.iuap.ucf.mdd.error.UCFRemoteServiceException;
import com.yonyoucloud.iuap.ucf.mdd.starter.core.module.network.ClientAddressUtil;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.domain.AccessTokenBO;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.service.TokenService;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.pojo.CommonAuthResult;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.auth.OpenApiAuthProviderFactory;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.auth.pojo.TenantUserIdInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/2 1:52 下午
 */
@Slf4j
@RestController
@RequestMapping(AuthController.PATH_AUTH_PREFIX)
@RequiredArgsConstructor
public class AuthController {

    public static final String PATH_AUTH_PREFIX = "/rest/v1/abpaas/isv/auth/";

    private final OpenApiAuthProviderFactory authProviderFactory;
    private final TokenService tokenService;
    private final ApplicationContext applicationContext;

    private final ISVAuthService isvAuthService;

    @GetMapping(value = "code", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CommonAuthResult authByUnifyCode(@RequestParam String code, HttpServletRequest request, HttpServletResponse response) {

        String isvTokenName = isvAuthService.getISVTokenName();

        TenantUserIdInfo info;
        try {
            info = authProviderFactory.getAuthProvider().queryUserByCode(code);
        } catch (UCFRemoteServiceException e) {
            throw new UCFAuthenticationFailedException("code invalid, code should be use in one minute", e);
        }

        CommonAuthResult authResult = processExistsCookie(request, isvTokenName);
        //防止串租户问题
        if (authResult != null && StringUtils.equals(info.getTenantId(), authResult.getTenantId())) {
            return authResult;
        }

        AccessTokenBO accessTokenBO = tokenService.generateToken(info.getTenantId(), info.getYhtUserId(), applicationContext.getId());
        String token = accessTokenBO.getToken();
        long expiry = (System.currentTimeMillis() - accessTokenBO.getExpirationAt()) / 1000;
        Cookie cookie = WebUtils.createCookie(isvTokenName, token, true, expiry, null);
        cookie.setDomain(ClientAddressUtil.getRootDomain(request));
        response.addCookie(cookie);

        authResult = new CommonAuthResult();
        authResult.setTenantId(info.getTenantId());
        authResult.setYhtUserId(info.getYhtUserId());
        return authResult;
    }

    private CommonAuthResult processExistsCookie(HttpServletRequest request, String isvTokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0){
            return null;
        }
        Cookie isvTokenCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(isvTokenName)).findAny().orElse(null);

        //如已有有效cookie就不再生成新的
        if (isvTokenCookie == null) {
            return null;
        }

        String token = isvTokenCookie.getValue();
        AccessTokenBO validate;
        try {
            validate = tokenService.validate(token);
        } catch (Exception e) {
            return null;
        }
        //token 5分钟内有效
        if (validate == null || validate.getTenantId() == null || validate.getExpirationAt() <= System.currentTimeMillis() + 1000 * 60 * 5) {
            return null;
        }
        CommonAuthResult authResult = new CommonAuthResult();
        authResult.setTenantId(validate.getTenantId());
        authResult.setYhtUserId(validate.getUserId());
        return authResult;
    }
}
