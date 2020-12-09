package com.yonyou.ucf.mdf.app.mobile.web.controller;

import com.yonyou.ucf.mdd.ext.controller.Authentication;
import com.yonyou.ucf.mdf.app.mobile.module.MobileCrossOriginStrategy;
import com.yonyou.ucf.mdf.app.mobile.module.MobileTemplateService;
import com.yonyou.ucf.mdf.app.mobile.module.MobileYhtContextSetupService;
import com.yonyou.ucf.mdf.app.mobile.module.YhtTokenCipherDecryptService;
import com.yonyou.ucf.mdf.app.mobile.web.config.MobileLocaleProbe;
import com.yonyou.ucf.mdf.app.mobile.web.controller.pojo.CommonResponse;
import com.yonyou.workbench.cons.Constants;
import com.yonyoucloud.iuap.ucf.mdd.starter.core.module.network.ClientAddressUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

@Authentication(false)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/app/")
public class MobileYhtAccessTokenController {

    private final MobileYhtContextSetupService yhtContextSetupService;

    private final MobileCrossOriginStrategy mobileCrossOriginStrategy;

    private final YhtTokenCipherDecryptService yhtTokenCipherDecryptService;

    private final MobileTemplateService mobileTemplateService;

    private final MobileLocaleProbe localeProbe = new MobileLocaleProbe();

    @RequestMapping(value = "index/yht/token/context")
    public CommonResponse auth(@RequestParam String yhtAccessTokenCipher, @RequestParam String userId, @RequestParam String tenantId,
                               @RequestParam(required = false) String billno,
                               @CookieValue(value = "wb_at", required = false) String diworkWebToken,
                               HttpServletRequest request, HttpServletResponse response) {
        mobileCrossOriginStrategy.writeAllowCrossDomainHeaders(request, response);
        try {
            String yhtAccessToken = yhtTokenCipherDecryptService.decrypt(yhtAccessTokenCipher);
            doAuth(yhtAccessToken, userId, tenantId, request, response);

            MobileContextResponse mobileContextResponse = new MobileContextResponse();
            if (StringUtils.isNotBlank(billno)) {
                boolean exists = isBillExists(billno, tenantId);
                mobileContextResponse.setMobileExists(exists);
            }
            return CommonResponse.success(mobileContextResponse);
        } catch (Exception e) {
            return CommonResponse.failed(e.getMessage(), "500010");
        }
    }

    private boolean isBillExists(String billno, String tenantId) {
        return mobileTemplateService.exists(billno, tenantId);
    }

    public void doAuth(String yhtAccessToken, String userId, String tenantId, HttpServletRequest request, HttpServletResponse response) {

        Locale locale = localeProbe.preHandle(request);
        Map<String, Object> context = yhtContextSetupService.buildYhtAccessTokenContext(tenantId, userId, locale);

        String newYhtToken = yhtContextSetupService.syncYhtAccessTokenContextInfo(yhtAccessToken, context);

        writeCookieToDevice(request, response, newYhtToken);
    }

    private void writeCookieToDevice(HttpServletRequest request, HttpServletResponse response, String yhtAccessToken) {
        Cookie cookie = new Cookie(Constants.YHT_ACCESS_TOKEN, yhtAccessToken);
        cookie.setDomain(ClientAddressUtil.getRootDomain(request));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MobileContextResponse {
        private Boolean mobileExists;
    }

}
