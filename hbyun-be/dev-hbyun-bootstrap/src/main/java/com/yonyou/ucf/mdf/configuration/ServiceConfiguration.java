package com.yonyou.ucf.mdf.configuration;

import com.yonyou.diwork.exception.BusinessException;
import com.yonyou.diwork.multilingual.model.LanguageVO;
import com.yonyou.diwork.multilingual.service.ILanguageService;
import com.yonyou.iuap.ml.provider.IMultiLangProvider;
import com.yonyou.iuap.ucf.common.ml.DefaultMultiLangProvider;
import com.yonyou.ucf.mdd.common.context.impl.MddEnvServiceImpl;
import com.yonyou.ucf.mdd.common.context.impl.MddSqlSessionServiceImpl;
import com.yonyou.ucf.mdd.common.interfaces.context.IMddCacheService;
import com.yonyou.ucf.mdd.ext.core.impl.MddPubCacheServiceImpl;
import com.yonyou.ucf.mdd.ext.i18n.service.IMddMultiLangEnumService;
import com.yonyou.ucf.mdd.ext.i18n.service.IMddMultiLangRefService;
import com.yonyou.ucf.mdd.ext.i18n.service.MddMultiLangBaseServiceImpl;
import com.yonyou.ucf.mdd.ext.i18n.service.impl.MddMultiLangEnumServiceImpl;
import com.yonyou.ucf.mdd.ext.i18n.service.impl.MddMultiLangMetaDaoServiceImpl;
import com.yonyou.ucf.mdd.ext.i18n.service.impl.MddMultiLangRefServiceImpl;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.ImportSingleService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportDataTypeCheckHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportPrivilegeCheckHandler;
import com.yonyou.ucf.mdd.ext.ref.service.impl.RefService;
import com.yonyou.ucf.mdd.ext.service.DefaultBillService;
import com.yonyou.ucf.mdd.ext.util.property.EnvironmentHelper;
import com.yonyou.ucf.mdd.poi.service.ImportLimit;
import com.yonyou.ucf.mdd.poi.service.ImportRefMul;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.List;

/**
 * FileName: ServiceConfiguration
 * Author: WP
 * Date: 2020/9/2 10:12
 * Description:
 * History:
 **/
@Configuration
public class ServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EnvironmentHelper environmentHelper() {
        return new EnvironmentHelper();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultBillService defaultBillService() {
        return new DefaultBillService();
    }

    @Bean
    @ConditionalOnMissingBean
    public IMddMultiLangRefService mddMultiLangRefServiceImpl() {
        return new MddMultiLangRefServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RefService refService() {
        return new RefService();
    }

    @Bean
    @ConditionalOnMissingBean
    public IMultiLangProvider multiLangProvider() {
        return new DefaultMultiLangProvider();
    }

    @Bean({"extendCache", "defaultCache"})
    @ConditionalOnMissingBean
    public IMddCacheService extendCache() {
        return new MddPubCacheServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddSqlSessionServiceImpl mddSqlSessionServiceImpl() {
        return new MddSqlSessionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportLimit mddImportLimit() {
        return new ImportLimit();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddEnvServiceImpl mddEnvService() {
        return new MddEnvServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportRefMul mddImportRefMul() {
        return new ImportRefMul();
    }

    @Bean
    @Primary
    public MddMultiLangBaseServiceImpl mddMultiLangBaseServiceImpl() {
        return new MddMultiLangBaseServiceImpl();
    }

    @Bean
    public MddMultiLangMetaDaoServiceImpl mddMultiLangMetaDaoService() {
        return new MddMultiLangMetaDaoServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportSingleService MddImportSingleService() {
        return new ImportSingleService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportPrivilegeCheckHandler importPrivilegeCheckHandler() {
        return new ImportPrivilegeCheckHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportDataTypeCheckHandler importDataTypeCheckHandler() {
        return new ImportDataTypeCheckHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public IMddMultiLangEnumService iMddMultiLangEnumService() {
        return new MddMultiLangEnumServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ILanguageService iLanguageService() {
        return new ILanguageService() {

            @Override
            public LanguageVO create(LanguageVO languageVO) throws BusinessException {
                return null;
            }

            @Override
            public Iterable<LanguageVO> create(Iterable<LanguageVO> iterable) throws BusinessException {
                return null;
            }

            @Override
            public void delete(LanguageVO languageVO) throws BusinessException {

            }

            @Override
            public LanguageVO update(LanguageVO languageVO) throws BusinessException {
                return null;
            }

            @Override
            public Iterable<LanguageVO> update(Iterable<LanguageVO> iterable) throws BusinessException {
                return null;
            }

            @Override
            public LanguageVO setDefaultLanague(String s, String s1) throws BusinessException {
                return null;
            }

            @Override
            public LanguageVO findByPrimaryKey(String s) {
                return null;
            }

            @Override
            public LanguageVO findByLangCode(String s, String s1) {
                return null;
            }

            @Override
            public Iterable<LanguageVO> findByLangCodes(String s, Collection<String> collection) {
                return null;
            }

            @Override
            public Iterable<LanguageVO> fetchByTenantIdSortByLangSequenceASC(String s, int i) {
                return null;
            }

            @Override
            public Iterable<LanguageVO> findAll(String s) {
                return null;
            }

            @Override
            public Iterable<LanguageVO> findAllWithoutCache(String s) {
                return null;
            }

            @Override
            public Iterable<LanguageVO> findAllEnable(String s) {
                return null;
            }

            @Override
            public List<LanguageVO> createIfNotExit(String s, List<LanguageVO> list) {
                return null;
            }

            @Override
            public List<LanguageVO> getAllLangVos() throws BusinessException {
                return null;
            }
        };
    }
}
