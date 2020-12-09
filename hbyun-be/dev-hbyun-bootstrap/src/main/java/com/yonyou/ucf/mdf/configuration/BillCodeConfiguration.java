package com.yonyou.ucf.mdf.configuration;

import com.yonyou.ucf.mdd.ext.dao.app.BillContextDao;
import com.yonyou.ucf.mdd.ext.dao.app.BillContextDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/9/17 15:20
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BillCodeConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public BillContextDao billContextDao() {
        return new BillContextDaoImpl();
    }

}
