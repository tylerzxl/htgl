package com.yonyou.ucf.mdd.isv.service;

import com.yonyou.ucf.mdd.api.interfaces.rpc.*;
import com.yonyou.ucf.mdd.ext.bill.meta.service.BillMetaService;
import com.yonyou.ucf.mdd.isv.rpc.impl.ISVBillQueryService;
import com.yonyou.ucf.mdd.isv.rpc.impl.ISVComOperateApiImpl;
import com.yonyou.ucf.mdd.isv.rpc.impl.ISVRuleApiImpl;
import com.yonyoucloud.enm.service.IEnmQueryService;
import com.yonyoucloud.iuap.ucf.mdd.error.UCFUnsupportedOperationException;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.DomainServiceFactory;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.bpm.BpmApi;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.refer.ReferApi;
import com.yonyoucloud.uretail.api.IBillQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p>Title</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-04-30$ 14:15$
 * @Version 1.0
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ISVServiceReferenceFactory implements ISVServiceFactory {

    private final DomainServiceFactory domainServiceFactory;

    private final ApplicationContext context;

    @Override
    public <T> T getService(Class<T> clz, String group, String version, Integer timeout) {
        return this.getRestService(clz, group);
    }

    @Override
    public ISVComOperateApiImpl getIComOperateApiProxy(String group, String version, Integer timeout) {
        return new ISVComOperateApiImpl();
    }

    @Override
    public IComQueryApi getIComQueryApiProxy(String group, String version, Integer timeout) {
        return domainServiceFactory.buildCommonQueryApi(group);
    }

    @Override
    public ReferApi getIRefApiProxy(String group, String version, Integer timeout) {
        return domainServiceFactory.refApi(group);
    }

    @Override
    public IRuleApi getIRuleApiProxy(String group, String version, Integer timeout) {
        return new ISVRuleApiImpl();
    }

    @Override
    public IUimetaApi getIUimetaApiProxy(String group, String version, Integer timeout) {
        return domainServiceFactory.uiMetaApi(group);
    }

    @Override
    public IBillQueryService getBillQueryService(String group, String version, Integer timeout) {
        IComQueryApi comQueryApi = getIComQueryApiProxy(group, version, timeout);
        ISVComOperateApiImpl comOperateApi = getIComOperateApiProxy(group, version, timeout);
        return new ISVBillQueryService(comQueryApi, comOperateApi);
    }

    @Override
    public IBillQueryService getBillQueryService(String group) {
        return getBillQueryService(group, null, null);
    }

    @Override
    public <T> T getService(Class<T> clz, String group) {
        return getService(clz, group, null, null);
    }

    @Override
    public IComOperateApi getIComOperateApiProxy(String group) {
        return getIComOperateApiProxy(group, null, null);
    }

    @Override
    public IComQueryApi getIComQueryApiProxy(String group) {
        return getIComQueryApiProxy(group, null, null);
    }

    @Override
    public IRefApi getIRefApiProxy(String group) {
        return getIRefApiProxy(group, null, null);
    }

    @Override
    public IRuleApi getIRuleApiProxy(String group) {
        return getIRuleApiProxy(group, null, null);
    }

    @Override
    public IEnmQueryService getIEnumQueryService(String group) {
        return domainServiceFactory.enumQueryApi(group);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRestService(Class<T> clazz, String group) {
        if (IUimetaApi.class.isAssignableFrom(clazz)) {
            return (T) getIUimetaApiProxy(group, null, null);
        }
        if (IRuleApi.class.isAssignableFrom(clazz)) {
            return (T) getIRuleApiProxy(null);
        }
        if (IComOperateApi.class.isAssignableFrom(clazz)) {
            return (T) getIComOperateApiProxy(null);
        }
        if (IComQueryApi.class.isAssignableFrom(clazz)) {
            return (T) getIComQueryApiProxy(group);
        }
        if (IRefApi.class.isAssignableFrom(clazz)) {
            return (T) getIRefApiProxy(group);
        }
        if (IBillQueryService.class.isAssignableFrom(clazz)) {
            return (T) getBillQueryService(group);
        }
        if (IEnmQueryService.class.isAssignableFrom(clazz)) {
            return (T) getIEnumQueryService(group);
        }
        try {
            return context.getBean(clazz);
        } catch (Exception e) {
            log.warn("bean {} not found from application context", clazz.getSimpleName());
        }
        throw new UCFUnsupportedOperationException("unsupported bean of class " + clazz.getSimpleName());
    }
}
