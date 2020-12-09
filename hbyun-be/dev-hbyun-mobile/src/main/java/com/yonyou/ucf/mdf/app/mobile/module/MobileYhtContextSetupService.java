package com.yonyou.ucf.mdf.app.mobile.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.diwork.exception.BusinessException;
import com.yonyou.diwork.exception.DiworkRuntimeException;
import com.yonyou.diwork.multilingual.service.ILanguageService;
import com.yonyou.diwork.service.pub.ITenantUserService;
import com.yonyou.iuap.bd.common.exception.BaseDocException;
import com.yonyou.iuap.data.service.itf.TenantStatusApi;
import com.yonyou.iuap.org.dto.TenantMultiOrgInfo;
import com.yonyou.iuap.tenant.status.entity.bo.TenantStatus;
import com.yonyou.workbench.cons.Constants;
import com.yonyou.workbench.dto.OptionsDTO;
import com.yonyou.workbench.util.SDKResultUtils;
import com.yonyou.yht.sdk.UserCenter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class MobileYhtContextSetupService {

    public Map<String, Object> buildYhtAccessTokenContext(String tenantId, String userId, Locale locale) {
        Map<String, Object> map = new HashMap<>();
        //租户ID
        map.put("tenantId", tenantId);
        //当前系统
        map.put("syscode", "U8C3");
        //设置业务日期
        map.put("businessDate", null);
        //产品线
        map.put(Constants.PRODUCT_LINE, "diwork");

        return map;
    }

    public String syncYhtAccessTokenContextInfo(String yhtAccessToken, Map<String, Object> context) {
        String syncResult = UserCenter.setUserCurrentTenant(yhtAccessToken, context);
        if (SDKResultUtils.getData(syncResult).isSuccess()) {
            return parseVersionToken(syncResult, yhtAccessToken);
        }
        String errorMessage = String
                .format("setUserCurrentTenant error!! token:%s,values:%s,result:%s", yhtAccessToken, JSON.toJSONString(context), syncResult);
        log.error(errorMessage);
        throw new DiworkRuntimeException("友户通服务异常", new RuntimeException(errorMessage));
    }

    private String parseVersionToken(String setTenantResult, String yhtAccessToken) {
        String versionToken = null;
        try {
            versionToken = JSON.parseObject(setTenantResult).getString("version");
        } catch (Exception e) {
            log.warn("exception when get version token", e);
        }
        if (StringUtils.isNotBlank(versionToken)) {
            return versionToken;
        }
        return yhtAccessToken;
    }

}
