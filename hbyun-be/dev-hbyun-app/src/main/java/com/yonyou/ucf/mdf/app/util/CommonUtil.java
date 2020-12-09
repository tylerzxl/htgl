package com.yonyou.ucf.mdf.app.util;


import com.yonyou.iuap.context.InvocationInfoProxy;
import com.yonyou.ucf.mdd.common.constant.MddConstants;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.common.interfaces.context.ISimpleUser;
import com.yonyou.ucf.mdd.common.interfaces.login.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;

@Slf4j
public class CommonUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getTenantId() {
        T tenantId = ApplicationContextUtil.getThreadContext("tenantId");
        if (tenantId == null || (tenantId instanceof String && StringUtils.isBlank((String) tenantId))) {
            tenantId = (T) InvocationInfoProxy.getTenantid();
        }
        return tenantId;
    }

    public static <T> void setTenantId(T yhtTenantId) {
        ApplicationContextUtil.setThreadContext("tenantId", yhtTenantId);
    }

    public static String getOrgId() {
        return ApplicationContextUtil.getThreadContext("getOrgId");
    }

    @SuppressWarnings("unchecked")
    public static <T> T getUserId() {
        T userId = ApplicationContextUtil.getThreadContext("userId");
        if (userId == null || (userId instanceof String && StringUtils.isBlank((String) userId))) {
            userId = (T) InvocationInfoProxy.getUserid();
        }
        return userId;
    }

    public static String getToken() {//TODO
        return ApplicationContextUtil.getThreadContext(MddConstants.PARAM_TOKEN);
    }

    public static String getSqlLogLevel() {
        return "DEBUG";
    }

    public static SqlSessionTemplate getCurrentSqlSession() {
        return ApplicationContextUtil.getBean("mainSqlSession", SqlSessionTemplate.class);
    }

    public static <T> T getTenantByToken(String token) {
        T tenantId = null;
        try {
            ILoginService loginService = MddBaseContext.getBean(ILoginService.class);
            if (loginService != null) {
                ISimpleUser user = loginService.getUserByYhtToken(token);
                tenantId = user.getTenantId();
            }
        } catch (Exception e) {
            log.error("通过token 获取user 失败 ： " + e.getMessage(), e);
        }
        return tenantId;
    }

    public static Object getContext(String key) {
        return ApplicationContextUtil.getThreadContext(key);
    }

    public static void setContext(String key, Object object) {
        ApplicationContextUtil.setThreadContext(key, object);
    }

    public static void delContext(String key) {
        ApplicationContextUtil.delContext(key);
    }

    public static void setToken(String yhtAccessToken) {
        ApplicationContextUtil.setThreadContext(MddConstants.PARAM_TOKEN, yhtAccessToken);
    }
}
