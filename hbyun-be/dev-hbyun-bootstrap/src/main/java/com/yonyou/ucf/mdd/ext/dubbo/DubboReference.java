package com.yonyou.ucf.mdd.ext.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.yonyou.cloud.bean.RemoteCallInfo;
import com.yonyou.cloud.middleware.AppRuntimeEnvironment;
import com.yonyou.cloud.mw.MwLocator;
import com.yonyou.cloud.mwclient.MwClientStartUp;
import com.yonyou.cloud.reqservice.IRemoteCallInfoManagerService;
import com.yonyou.ucf.mdd.common.context.MddBaseContext;
import com.yonyou.ucf.mdd.ext.core.AppContext;
import com.yonyou.ucf.mdd.ext.middleware.IrisReference;
import com.yonyou.ucf.mdd.ext.util.Logger;
import com.yonyou.ucf.mdd.isv.service.ISVServiceFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DubboReference<T> {

    // 当前应用的信息
    private static final ApplicationConfig application = new ApplicationConfig();
    // 注册中心信息缓存
    private static final Map<String, RegistryConfig> registryConfigCache = new ConcurrentHashMap<>();
    private static final String DUBBO_TYPE = "dubbo";
    private static final String IRIS_TYPE = "iris";
    private static String rpcType;
    private static volatile DubboReference instance;

    static {
        application.setName("uretail-dyn");
        try {
            Class.forName("com.yonyou.cloud.mw.MwLocator", false, DubboReference.class.getClassLoader());
            rpcType = System.getProperty("uretailRpcType");
        } catch (ClassNotFoundException e) {
            rpcType = DUBBO_TYPE;
        }
    }

    private final Map<String, IrisReference<T>> irisReferenceCache = new ConcurrentHashMap<>();
    // 各个业务方的ReferenceConfig缓存
    private Map<String, ReferenceConfig<T>> referenceCache = null;


    protected DubboReference() {

    }

    public static DubboReference getInstance() {
        if (instance == null) {
            synchronized (DubboReference.class) {
                if (instance == null) {
                    instance = new DubboReference();
                }
            }
        }
        return instance;
    }

    /**
     * 获取注册中心信息
     *
     * @param address zk注册地址
     * @return
     */
    private static RegistryConfig getRegistryConfig(String address) {
        String key = address;
        /*
         * if(null!=group){ key+= "-" + group; } if(null!=version){ key+= "-" +
         * version; }
         */
        RegistryConfig registryConfig = registryConfigCache.get(key);
        if (null == registryConfig) {
            registryConfig = new RegistryConfig();
            registryConfig.setAddress(address);
            registryConfig.setProtocol("zookeeper");
            // if(null!=group) registryConfig.setGroup(group);
            // if(null!=version) registryConfig.setVersion(version);
            registryConfigCache.put(key, registryConfig);
        }
        return registryConfig;
    }

    private static String getRegistryAddress(String domain) {

        String address = getProperty(null != domain ? domain + ".dubbo.registry.address" : "dubbo.registry.address");
        if (null == address) {
            address = getProperty("dubbo.registry.address");
        }
        if (null == address) {
            address = "127.0.0.1:2181";
        }
        return address;

    }

    public static String getProperty(String property) {

        String value = null;
        if (null != AppContext.getAppConfig()) {
            value = AppContext.getAppConfig().getProperty(property);
        }
        if (null == value) {
            if ("domains.use.uretail.tenant".equals(property)) {
                value = "producter";
            }
        }
        return value;
    }

    private Map<String, ReferenceConfig<T>> referenceCache() {
        if (null == referenceCache) {
            referenceCache = new ConcurrentHashMap<>();
        }

        return referenceCache;
    }

    /**
     * 获取服务的代理对象
     *
     * @param address
     * @param group
     * @return
     */
    private ReferenceConfig<T> getReferenceConfig(Class<T> clazz, String address, String group, String version, Integer timeout) {
        String referenceKey = clazz.getName() + group;
        ReferenceConfig<T> referenceConfig = referenceCache()
                .get(referenceKey);
        if (null == referenceConfig) {
            referenceConfig = createReferenceConfig(clazz, address, group, version,
                    referenceKey, timeout);
        }
        if (null != timeout && null != referenceConfig) {
            referenceConfig.setTimeout(timeout);
        }
        return referenceConfig;
    }

    private ReferenceConfig<T> createReferenceConfig(Class<T> clazz, String address, String group, String version, String referenceKey,
                                                     Integer timeout) {
        ReferenceConfig<T> referenceConfig = new ReferenceConfig<>();
        application.setName("uretail-dyn-" + group);
        referenceConfig.setApplication(application);
        referenceConfig.setRegistry(getRegistryConfig(address));
        referenceConfig.setInterface(clazz);
        if (null != group) {
            referenceConfig.setGroup(group);
        }
        if (null != version) {
            referenceConfig.setVersion(version);
        }

        if (null != timeout && null != referenceConfig) {
            referenceConfig.setTimeout(timeout);
        }
        referenceCache().put(referenceKey, referenceConfig);
        return referenceConfig;
    }

    private Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 调用远程服务
     *
     * @param group
     * @param version
     * @return
     * @throws IOException
     */
    public T getReference(Class<T> clazz, String group, String version) {
        return getReference(clazz, group, version, null);
    }


    public T getReference(Class<T> clazz, String group, String version, Integer timeout) {
        ISVServiceFactory isvBeanService = AppContext.getBean(ISVServiceFactory.class);
        T service = isvBeanService.getService(clazz, group, version, timeout);
        if (service != null) {
            return service;
        }

        T result;
        Logger.info("rpc type配置项为={}, group={}", rpcType, group);
        //无rpc type配置项 先走iris 如无实例则走dubbo
        if (null == rpcType || "".equals(rpcType)) {
            result = getIrisiBillQueryService(clazz, group);
            if (null != result) {
                Logger.info("获取iris ref成功! group: {}", group);
                return result;
            }
            Logger.info("获取iris ref失败! group:{}", group);
            return getDubboiBillQueryService(clazz, group, version, timeout);
        } else if (DUBBO_TYPE.equals(rpcType)) {
            return getDubboiBillQueryService(clazz, group, version, timeout);
        } else if (IRIS_TYPE.equals(rpcType)) {
            if (group.equals(AppRuntimeEnvironment.getAppCode())) {
                return getDubboiBillQueryService(clazz, group, version, timeout);
            }
            //无需判断注册中心是否存在实例
            return getIrisRef(group, clazz, false);
        }
        result = getIrisiBillQueryService(clazz, group);
        if (null != result) {
            Logger.info("获取iris ref成功");
            return result;
        }
        Logger.info("获取iris ref失败! group:{}", group);
        return getDubboiBillQueryService(clazz, group, version, timeout);
    }

    private T getIrisiBillQueryService(Class clazz, String group) {
        T irisRef = getIrisRef(group, clazz, true);
        return irisRef;
    }

    private T getIrisRef(String group, Class<?> interfaceClass, boolean check) {
        //iris 未启动
        if (!MwClientStartUp.getstatus()) {
            Logger.warn("iris rpc 未启动");
            return null;
        }
        //如果为本域查询 则获取本地的实现类
        if (group.equals(AppRuntimeEnvironment.getAppCode())) {
            Collection<RemoteCallInfo> rcis = MwLocator.lookup(IRemoteCallInfoManagerService.class).getAllRemoteCallInfo();
            Optional<RemoteCallInfo> remoteCallInfo = rcis.stream().filter(rci -> interfaceClass.getName().equals(rci.getClassName())).findFirst();
            if (remoteCallInfo.isPresent()) {
                RemoteCallInfo rci = remoteCallInfo.get();
                String beanid = rci.getBeanId();
                if (StringUtils.isNotBlank(beanid)) {// 多实现的时候必须要注册实现的beanid
                    return (T) MddBaseContext.getBean(beanid, interfaceClass);
                } else {//单实现的时候beanid可能为空
                    return (T) MddBaseContext.getBean(interfaceClass);
                }
            }

            return null;
        }
        try {
			/*String baseDomain=AppContext.getAppConfig().getProperty("base.domain");
			if(!group.equals(baseDomain)){*/
            try {
                AppContext.bulidResCacheMap(true);
            } catch (Exception e) {
                Logger.error("bulidResCacheMap", e);
            }
            //}
            Logger.debug("iris 延用dubbo调用设置rpcToken的逻辑");
        } catch (Exception e) {
            Logger.error("iris 延用dubbo调用设置rpcToken的逻辑异常", e);
        }
        IrisReference<T> result;
        String irisCacheKey = interfaceClass.getName() + group;
        if (irisReferenceCache.containsKey(irisCacheKey)) {
            result = irisReferenceCache.get(irisCacheKey);
        } else {
            result = new IrisReference<>(interfaceClass, group);
            irisReferenceCache.put(irisCacheKey, result);
        }
        return check ? result.getRef() : result.getRefNoCheck();
    }

    public T getDubboiBillQueryService(Class<T> clazz, String group,
                                       String version, Integer timeout) {
        //String externalDomain=AppContext.getAppConfig().getProperty("externalDomain");
        String domain = group;  //外部域
		/*if(!Toolkit.isEmpty(externalDomain)){
			if((externalDomain+",").indexOf(group+",")>-1){
				domain=group;
			}
		}*/
        ReferenceConfig<T> reference = getReferenceConfig(clazz,
                getRegistryAddress(domain), group, version, timeout);
        if (null != reference) {
            T billQueryService = reference.get();
            if (null == billQueryService) {//再试一次
                reference = createReferenceConfig(clazz,
                        getRegistryAddress(domain), group, version, group, timeout);
                billQueryService = reference.get();
            }

            if (null != domain) {
				/*String baseDomain=AppContext.getAppConfig().getProperty("base.domain");
				if(!domain.equals(baseDomain)){*/
                try {
                    AppContext.bulidResCacheMap(true);
                } catch (Exception e) {
                    Logger.error("bulidResCacheMap", e);
                }
                //}

            }
            return billQueryService;
        }
        return null;
    }
}
