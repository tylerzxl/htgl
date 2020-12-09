package com.yonyou.ucf.mdf.configuration;

import com.yonyou.ucf.mdd.ext.dao.meta.builder.ESSimpleSqlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imeta.orm.crud.*;
import org.imeta.orm.query.QuerySqlBuilder;
import org.imeta.orm.ref.ReferenceSqlBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/9/24 22:24
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SqlBuilderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public QuerySqlBuilder querySqlBuilder() {
        return new QuerySqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public BatchDeleteSqlBuilder batchDeleteSqlBuilder() {
        return new BatchDeleteSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public BatchUpdateSqlBuilder batchUpdateSqlBuilder() {
        return new BatchUpdateSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public DeletePartitionSqlBuilder deletePartitionSqlBuilder() {
        return new DeletePartitionSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public DeleteSqlBuilder deleteSqlBuilder() {
        return new DeleteSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public InsertSqlBuilder insertSqlBuilder() {
        return new InsertSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceSqlBuilder referenceSqlBuilder() {
        return new ReferenceSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public SelectSqlBuilder selectSqlBuilder() {
        return new SelectSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdatePartitionSqlBuilder updatePartitionSqlBuilder() {
        return new UpdatePartitionSqlBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public UpdateSqlBuilder updateSqlBuilder() {
        return new UpdateSqlBuilder();
    }

}
