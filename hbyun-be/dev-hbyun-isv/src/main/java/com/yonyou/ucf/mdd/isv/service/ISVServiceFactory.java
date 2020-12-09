package com.yonyou.ucf.mdd.isv.service;

import com.yonyou.ucf.mdd.api.interfaces.rpc.*;
import com.yonyou.ucf.mdd.core.interfaces.rpc.RPCServiceAdapter;
import com.yonyoucloud.enm.service.IEnmQueryService;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.isv.router.module.bpm.BpmApi;
import com.yonyoucloud.uretail.api.IBillQueryService;

/**
 * <p>Title</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-05-13$ 11:07$
 * @Version 1.0
 **/
public interface ISVServiceFactory extends RPCServiceAdapter {

    IBillQueryService getBillQueryService(String group, String version, Integer timeout);

    IBillQueryService getBillQueryService(String group);

    <T> T getService(Class<T> clz, String group);

    IComOperateApi getIComOperateApiProxy(String group);

    IComQueryApi getIComQueryApiProxy(String group);

    IRefApi getIRefApiProxy(String group);

    IRuleApi getIRuleApiProxy(String group);

    IEnmQueryService getIEnumQueryService(String group);
}
