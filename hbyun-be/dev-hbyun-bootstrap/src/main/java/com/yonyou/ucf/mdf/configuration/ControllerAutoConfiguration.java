package com.yonyou.ucf.mdf.configuration;

import com.yonyou.ucf.mdd.ext.bill.controller.BillMetaController;
import com.yonyou.ucf.mdd.ext.bill.controller.BillStatusController;
import com.yonyou.ucf.mdd.ext.controller.RefController;
import com.yonyou.ucf.mdd.ext.enums.controller.EnumController;
import com.yonyou.ucf.mdd.ext.filter.controller.FilterController;
import com.yonyou.ucf.mdd.ext.filter.controller.FilterDesignController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/9/17 22:12
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ControllerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RefController refController() {
        return new RefController();
    }

    @Bean
    @ConditionalOnMissingBean
    public BillStatusController billStatusController() {
        return new BillStatusController();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterDesignController filterDesignController() {
        return new FilterDesignController();
    }

    @Bean
    @ConditionalOnMissingBean
    public EnumController enumController(){
        return new EnumController();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterController filterController(){
        return new FilterController();
    }

    @Bean
    @ConditionalOnMissingBean
    public BillMetaController billMetaController(){
        return new BillMetaController();
    }
}
