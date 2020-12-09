package com.yonyou.ucf.mdf.configuration;

import com.yonyou.uap.tenant.service.itf.ITenantRoleUserService;
import com.yonyou.uap.tenantauth.entity.TenantRole;
import com.yonyou.uap.tenantauth.entity.TenantRoleUser;
import com.yonyou.ucf.mdd.ext.poi.api.ImportSheetDataChecking;
import com.yonyou.ucf.mdd.ext.poi.impl.DefaultSheetDataCheckingImpl;
import com.yonyou.ucf.mdd.ext.poi.importbiz.conver.ConverService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.event.LogEventService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.init.ImportServiceInit;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.ImportCommandService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.common.ImportProgressService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.common.ImportResultHandleService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.common.ImportTransferService;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.GrandchildrenDataHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportAttachmentHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportBankHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportBeforeCheckHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportDataConverHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.handle.ImportDataTransHandler;
import com.yonyou.ucf.mdd.ext.poi.importbiz.service.impl.ImportStatusServiceImpl;
import com.yonyou.ucf.mdd.poi.itf.IPOIService;
import com.yonyou.ucf.mdd.poi.service.*;
import org.assertj.core.util.Lists;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/8/15 10:40 上午
 */
@Configuration
public class POIAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public IPOIService mddPOIService() {
        return new POIService();
    }


    @Bean
    @ConditionalOnMissingBean
    public ImportEventService mddImportEventService() {
        return new ImportEventService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportSaveBankDataService mddImportSaveBankDataService() {
        return new ImportSaveBankDataService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportSupplySelfDefinitionItem mddImportSupplySelfDefinitionItem() {
        return new ImportSupplySelfDefinitionItem();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportSheetDataChecking mddExtDataCheckingService() {
        return new DefaultSheetDataCheckingImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportDataTypeCheck mddImportDataTypeCheck() {
        return new ImportDataTypeCheck();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventImportCheckHelper mddEventImportCheckHelper() {
        return new EventImportCheckHelper();
    }


    @Bean
    @ConditionalOnMissingBean
    public ITenantRoleUserService tenantRoleUserService() {
        return new ITenantRoleUserService() {
            @Override
            public void deleByUserAndRole(String userId, String roleId) throws Exception {

            }

            @Override
            public void deleByUserAndRole(List<String> userIds, String roleId) throws Exception {

            }

            @Override
            public void deleByUserAndRole(String userId, List<String> roleIds) throws Exception {

            }

            @Override
            public void deleByUserAndRole(String userId, String roleId, String tenantId, String sysId) throws Exception {

            }

            @Override
            public List<TenantRoleUser> saveRoles(List<TenantRoleUser> trus) {
                return Lists.emptyList();
            }

            @Override
            public TenantRoleUser saveRole(TenantRoleUser tru) throws Exception {
                return tru;
            }

            @Override
            public List<String> findRoleIdsByUserId(String userId, String tenantId, String systemCode) {
                return Lists.emptyList();
            }

            @Override
            public List<TenantRole> findRolesByUserId(String userId, String tenantId, String systemCode) throws Exception {
                return Lists.emptyList();
            }

            @Override
            public Map<String, List<String>> findRoleIdsByTenantAndSystemCode(String tenantId, String systemCode) throws Exception {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, List<TenantRole>> findRolesByUserIds(List<String> userIds, String tenantId, String systemCode) throws Exception {
                return Collections.emptyMap();
            }

            @Override
            public Page<TenantRoleUser> getRoleUserPage(String roleId, String tenantId, String systemCode, int pageNumber, int pageSize) {
                return new PageImpl<>(Lists.emptyList());
            }

            @Override
            public List<String> findByRoleId(String roleId) throws Exception {
                return Lists.emptyList();
            }

            @Override
            public List<String> findUserIdsByTenantIdAndRoleCode(String tenantId, String roleCode, String userCode) {
                return Lists.emptyList();
            }

            @Override
            public void updateRoleCode(TenantRole dbRole) {

            }

            @Override
            public List<String> findUserIdsByRoleIdsAndTenantId(Iterable<String> roleIds, String tenantId) {
                return Lists.emptyList();
            }

            @Override
            public List<String> findUserIdsByRoleIds(List<String> roleIds) {
                return Lists.emptyList();
            }

            @Override
            public boolean checkExistByUserAndRoleCode(String userId, String tenantId, String sysId, String roleCode) {
                return false;
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportCommandService importCommandService() {
        return new ImportCommandService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportServiceInit importServiceInit() {
        return new ImportServiceInit();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportResultHandleService importResultHandleService() {
        return new ImportResultHandleService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportProgressService importProgressService() {
        return new ImportProgressService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConverService converService() {
        return new ConverService();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogEventService logEventService() {
        return new LogEventService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportTransferService importTransferService() {
        return new ImportTransferService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportStatusServiceImpl ImportStatusService() {
        return new ImportStatusServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportBeforeCheckHandler importBeforeCheckHandler() {
        return new ImportBeforeCheckHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportAttachmentHandler importAttachmentHandler() {
        return new ImportAttachmentHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportDataConverHandler importDataConverHandler() {
        return new ImportDataConverHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrandchildrenDataHandler grandchildrenDataHandler() {
        return new GrandchildrenDataHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportBankHandler importBankHandler() {
        return new ImportBankHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImportDataTransHandler importDataTransHandler() {
        return new ImportDataTransHandler();
    }



}
