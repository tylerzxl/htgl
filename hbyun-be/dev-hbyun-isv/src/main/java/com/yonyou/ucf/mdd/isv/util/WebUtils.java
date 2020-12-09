package com.yonyou.ucf.mdd.isv.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * <p>工具类</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-03-31$ 16:54$
 * @Version 1.0
 **/
public abstract class WebUtils extends org.springframework.web.util.WebUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

    /**
     * @return boolean
     * @Author chouhl
     * @Description 判断请求是否ajax请求
     * @Date 2020-03-31 17:03
     * @Param [request]
     **/
    public static boolean isAjax(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        boolean isAjax = false;

        String requestType = request.getHeader("X-Requested-With");
        if (StringUtils.equals("XMLHttpRequest", requestType)) {
            isAjax = true;
        }

        return isAjax;
    }

    /**
     * @return void
     * @Author chouhl
     * @Description 返回错误信息
     * @Date 2020-03-31 17:03
     * @Param [httpServletRequest, httpServletResponse, status, value]
     **/
    public static void sendErrorMsg(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int status, String value) {
        if (isAjax(httpServletRequest)) {
            try {
                httpServletResponse.sendError(status, value);
            } catch (IOException e) {
                LOGGER.error("httpServletResponse.sendError failed", null, e);
            }
        } else {
            httpServletResponse.setStatus(status);
            httpServletResponse.addHeader("errmsg", value);
        }
    }

    /**
     * @return javax.servlet.http.Cookie
     * @Author chouhl
     * @Description 生成cookie
     * @Date 2020-03-31 18:02
     * @Param [key, value, httpOnly, expiry, path]
     **/
    public static Cookie createCookie(String key, String value, boolean httpOnly, long expiry, String path) {
        Cookie cookie = null;

        try {
            cookie = new Cookie(key, URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            cookie = new Cookie(key, value);
        }
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(new Long(expiry).intValue());
        if (path != null) {
            cookie.setPath(path);
        } else {
            cookie.setPath("/");
        }
        cookie.setDomain("yyuap.com");

        return cookie;
    }

    /**
     * @return javax.servlet.http.Cookie
     * @Author chouhl
     * @Description 注销cookie
     * @Date 2020-06-08 13:13
     * @Param [key, path]
     **/
    public static Cookie expireCookie(String key, String path) {
        Cookie cookie = new Cookie(key, null);

        cookie.setMaxAge(0);
        if (path != null) {
            cookie.setPath(path);
        } else {
            cookie.setPath("/");
        }
        cookie.setDomain("yyuap.com");

        return cookie;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
