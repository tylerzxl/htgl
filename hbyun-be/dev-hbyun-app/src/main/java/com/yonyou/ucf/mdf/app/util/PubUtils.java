package com.yonyou.ucf.mdf.app.util;

import com.yonyou.iuap.utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PubUtils {
    public static Map<String, String> genYHTAccessTokenReqHeader(HttpServletRequest request) {
        Map<String, String> mapHeader = new HashMap<>();
        if (null != request) {
            String yhtAccessToken = CookieUtil.findCookieValue(request.getCookies(), "yht_access_token");
            String diworkSessionId = CookieUtil.findCookieValue(request.getCookies(), "wb_at");
            mapHeader.put("Cookie", "yht_access_token=" + yhtAccessToken + "; wb_at=" + diworkSessionId);
        }

        return mapHeader;
    }
}
