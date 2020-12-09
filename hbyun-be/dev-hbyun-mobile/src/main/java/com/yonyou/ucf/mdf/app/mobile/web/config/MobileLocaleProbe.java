package com.yonyou.ucf.mdf.app.mobile.web.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuhaoi
 */
public class MobileLocaleProbe {

    private static final Pattern UPESN_LOCALE_PATTERN = Pattern.compile("youZoneLanguage=(\\S+)\\s?");

    public Locale preHandle(HttpServletRequest request) {

        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        //仅处理友空间
        if (StringUtils.isBlank(userAgent) || !userAgent.contains("QYZone")) {
            return Locale.CHINA;
        }
        Matcher matcher = UPESN_LOCALE_PATTERN.matcher(userAgent);
        String language = null;
        if (matcher.find()) {
            language = matcher.group(1);
        }

        if (StringUtils.isBlank(language)) {
            return Locale.CHINA;
        }

        switch (language) {
            case "tw":
            case "zht":
            case "zh_TW":
            case "zh_HK":
                return Locale.TAIWAN;
            case "en":
            case "en_US":
                return Locale.US;
            case "zh":
            case "zh_CN":
            case "zhs":
            default:
                return Locale.CHINA;
        }
    }
}
