package com.yonyou.ucf.mdf.configuration;

import com.yonyou.ucf.mdd.common.interfaces.ref.IRefEventAdapter;
import com.yonyou.ucf.mdd.core.meta.MetaDaoDataAccessProxy;
import com.yonyou.ucf.mdd.core.service.RefEventAdapterImpl;
import com.yonyou.ucf.mdd.ext.bill.rule.crud.UIMetaExtLoadRule;
import com.yonyou.ucf.mdd.ext.dao.meta.service.QuerySchemaServiceImpl;
import com.yonyou.ucf.mdd.ext.ref.adapter.MetaExtRefDataHandler;
import org.imeta.biz.base.BizContext;
import org.imeta.orm.base.OrmContext;
import org.imeta.orm.dialect.support.MySqlDialect;
import org.imeta.orm.schema.QuerySchemaServiceProxy;
import org.imeta.spring.base.UnfiedBeanFactory;
import org.imeta.spring.support.cache.UnifiedMetaProperties;
import org.imeta.spring.support.db.ModelManager;
import org.imeta.spring.support.id.IdManager;
import org.imeta.spring.support.orm.QuerySchemaHandlerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/9 10:50 上午
 */
@Configuration
public class IMetaAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public UIMetaExtLoadRule uimetaExtLoadRule() {
        return new UIMetaExtLoadRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaDaoDataAccessProxy localDataAccessProxy() {
        return new MetaDaoDataAccessProxy();
    }

    /**
     * 修复编码规则检查重复不正确的问题
     *
     * @return
     */
    @Bean
    @Primary
    public UnfiedBeanFactory unfiedBeanFactory() {
        UnfiedBeanFactory unfiedBeanFactory = new UnfiedBeanFactory();
        unfiedBeanFactory.setConfigLocation("classpath:imeta-config.properties");
        return unfiedBeanFactory;
    }


    @Bean
    @ConditionalOnMissingBean
    public QuerySchemaServiceProxy mddQuerySchemaServie() {
        return new QuerySchemaServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public QuerySchemaHandlerAdapter serviceDataFetchHandler(QuerySchemaServiceProxy mddQuerySchemaServie) {
        QuerySchemaHandlerAdapter serviceDataFetchHandler = new QuerySchemaHandlerAdapter();
        serviceDataFetchHandler.setProxy(mddQuerySchemaServie);
        return serviceDataFetchHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public BizContext bizContext() {
        return new BizContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrmContext ormContext() {
        return new OrmContext();
    }

    @Bean
    @ConditionalOnMissingBean
    public MySqlDialect mysql() {
        return new MySqlDialect();
    }

    @Bean
    public ModelManager modelManager() {
        return new ModelManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdManager idManager(UnfiedBeanFactory factory, MetaDaoDataAccessProxy proxy) {
        factory.setLocalDataAccessProxy(proxy);
        return new IdManager(1L, 1L);
    }

    @Bean
    @ConditionalOnMissingBean
    public IRefEventAdapter refEventAdapter() {
        return new RefEventAdapterImpl();
    }

    /**
     * 升级到imeta-2.0.21, 兼容使用
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public UnifiedMetaProperties unifiedMetaProperties() {
        return UnifiedMetaProperties.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public MetaExtRefDataHandler metaExtRefDataHandler() {
        return new MetaExtRefDataHandler();
    }

}
