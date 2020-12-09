package com.yonyou.ucf.mdf.app.controller.interceptor;

import com.yonyou.diwork.multilingual.model.LanguageVO;
import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.model.SimpleUser;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.ext.model.LoginUser;
import com.yonyou.ucf.mdd.ext.poi.constant.POIConstant;
import com.yonyou.ucf.mdf.app.controller.interceptor.pojo.AuthRequestBO;
import com.yonyou.ucf.mdf.app.service.impl.ISVAuthService;
import com.yonyoucloud.iuap.ucf.mdd.starter.core.module.data.cache.StringKeyCache;
import com.yonyoucloud.iuap.ucf.mdd.starter.core.module.data.cache.redis.UCFRedisCacheFactory;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.domain.AccessTokenBO;
import com.yonyoucloud.iuap.ucf.mdd.starter.token.service.TokenService;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.CommonAuthApi;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.pojo.CommonAuthResult;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.auth.pojo.TokenResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.stream.Stream;

import static com.yonyou.ucf.mdf.app.service.impl.ISVAuthService.*;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/1 9:56 下午
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CookieValueInterceptor extends HandlerInterceptorAdapter {

    private final CommonAuthApi authApi;
    private final TokenService tokenService;
    private final ISVAuthService authService;

    private final UCFRedisCacheFactory cacheFactory;
    private StringKeyCache<TokenResult> tempTokenCache;

    @PostConstruct
    public void init() {
        tempTokenCache = cacheFactory.buildStringKeyCache("tempTokenCache", 1000, 20000, Duration.ofMinutes(10), true);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return true;
        }
        buildInvocationInfoContext(cookies);
        String tenantId = InvocationInfoProxy.getTenantid();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = request.getParameter(TENANT_ID_CAMEL_CASE);
        }
        String userId = InvocationInfoProxy.getUserid();
        String yhtAccessToken = String.valueOf(InvocationInfoProxy.getExtendAttribute(YHT_ACCESS_TOKEN));

        buildAppContext(tenantId, userId, yhtAccessToken);
        buildMddBaseContext(tenantId, userId, yhtAccessToken);
        return true;
    }

    private void buildMddBaseContext(String tenantId, String userId, String yhtAccessToken) {
        SimpleUser user = new SimpleUser();

        user.setTenant(tenantId);
        user.setId(userId);
        MddBaseContext.setCurrentUser(user);
        MddBaseContext.setTenantId(tenantId);
        MddBaseContext.setUserId(userId);
        MddBaseContext.setThreadContext("yhtTenantId", tenantId);
        MddBaseContext.setToken(yhtAccessToken);
        MddBaseContext.setThreadContext(POIConstant.IMPORT_MULLANG_CODES, LanguageVO.DEFAULT_LIST.get("diwork"));
    }

    private void buildInvocationInfoContext(Cookie[] cookies) {
        AuthRequestBO authRequestBO = new AuthRequestBO();
        Stream.of(cookies).forEach(cookie -> {
            if (USER_ID.equals(cookie.getName())) {
                authRequestBO.setCookieUserId(cookie.getValue());
            }
            if (TENANT_ID.equals(cookie.getName())) {
                authRequestBO.setCookieTenantId(cookie.getValue());
            }
            if (authService.getISVTokenName().equals(cookie.getName())) {
                AccessTokenBO token = tokenService.validate(cookie.getValue());
                authRequestBO.setTrustedTenantId(token.getTenantId());
                authRequestBO.setTrustedUserId(token.getUserId());
                authRequestBO.setToken(token);
            }
        });

        InvocationInfoProxy.setTenantid(authRequestBO.getTenantId());
        InvocationInfoProxy.setUserid(authRequestBO.getUserId());

        // 兼容处理没有友户通Token的问题 TODO change this implementation
        if (authRequestBO.trusted()) {
            TokenResult tempToken = queryToken(authRequestBO.getToken());
            String token = tempToken.getToken();
            InvocationInfoProxy.setToken(token);
            InvocationInfoProxy.setExtendAttribute(YHT_ACCESS_TOKEN, token);
        }
    }


    private TokenResult queryToken(AccessTokenBO token) {
        TokenResult value = tempTokenCache.getV(token.getToken());
        if (value != null && value.getExpireAt() > System.currentTimeMillis()) {
            return value;
        }
        TokenResult tempToken = authApi.generateToken(token.getTenantId(), token.getUserId());
        tempTokenCache.putVK(tempToken, token.getToken());
        return tempToken;
    }

    private void buildAppContext(String tenantId, String userId, String yhtAccessToken) {
        LoginUser user = buildUser(tenantId, userId, yhtAccessToken);
        try {
            AppContext.setToken(yhtAccessToken);
            AppContext.setCurrentUser(yhtAccessToken, user);
        } catch (Exception e) {
            log.warn("{} when update app context current user {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public LoginUser buildUser(String tenantId, String userId, String yhtAccessToken) {
        LoginUser user = new LoginUser();
        user.setYhtTenantId(tenantId);
        user.setYhtUserId(userId);
        user.setId(-1L);
        user.setYhtAccessToken(yhtAccessToken);
        return user;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        InvocationInfoProxy.reset();
        try {
            AppContext.clear();
        } catch (Exception e) {
            // for code as shit
            log.debug("exception when clear app context");
        }
        MddBaseContext.clearThreadLocalContext();
    }
}
