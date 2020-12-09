package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdd.api.interfaces.rpc.IRefApi;
import com.yonyou.ucf.mdd.api.interfaces.rpc.IUimetaApi;
import com.yonyou.ucf.mdd.common.model.uimeta.filter.vo.FilterVO;
import com.yonyou.ucf.mdd.common.utils.json.GsonHelper;
import com.yonyou.ucf.mdd.core.interfaces.rpc.RPCServiceAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class BaseController {

    @Autowired
    private RPCServiceAdapter serviceAdapter;

    protected IRefApi getRefApi(String domain, String version, Integer timeout) {
        return serviceAdapter.getIRefApiProxy(domain, version, timeout);
    }

    protected IUimetaApi getUIMetaApi() {
        return serviceAdapter.getIUimetaApiProxy(null, null, null);
    }

    protected IUimetaApi getUIMetaApi(String domain, String version, Integer timeout) {
        return serviceAdapter.getIUimetaApiProxy(domain, version, timeout);
    }

    protected void renderJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding(Consts.UTF_8.name());
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        try {
            response.getWriter().write(json);
        } catch (IOException e) {
            log.error("exception when render json",e);
        }
    }

    protected FilterVO parseFilterVO(String condition, FilterVO filterVO, HttpServletRequest request) {
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            return (FilterVO) GsonHelper.FromJSon(condition, FilterVO.class);
        } else if (request.getMethod().equals(RequestMethod.POST.name())) {
            return filterVO;
        } else {
            return new FilterVO();
        }
    }

}
