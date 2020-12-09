package com.yonyou.ucf.mdf.configuration;

import com.yonyou.ucf.mdd.ext.bill.rule.check.CheckUniqueRule;
import com.yonyou.ucf.mdd.ext.bill.rule.crud.*;
import com.yonyou.ucf.mdd.ext.pub.rule.MddExtFormulaRule;
import com.yonyou.ucf.mdd.isv.rpc.rule.ISVSubmitBillRule;
import com.yonyou.ucf.mdd.isv.rpc.rule.ISVUnsubmitBillRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
public class MddExtRuleConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DeleteBillRule deleteBillRule() {
        return new DeleteBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public CheckUniqueRule checkUniqueRule() {
        return new CheckUniqueRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public DetailBillRule detailBillRule() {
        return new DetailBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MoveprevBillRule moveprevBillRule() {
        return new MoveprevBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MovefirstBillRule movefirstBillRule() {
        return new MovefirstBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MovelastBillRule movelastBillRule() {
        return new MovelastBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MovenextBillRule movenextBillRule() {
        return new MovenextBillRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public QuerytreeBillRule querytreeBillRule(){
        return new QuerytreeBillRule();
    }

    @Bean
    @Primary
    @ConditionalOnMissingClass
    public ISVSubmitBillRule submitBillRule(){
        return new ISVSubmitBillRule();
    }


    @Bean
    @ConditionalOnMissingClass
    public ISVUnsubmitBillRule unsubmitBillRule() {
        return new ISVUnsubmitBillRule();
    }

    @Bean
    @ConditionalOnMissingClass
    public MddExtFormulaRule mddExtFormulaRule(){
        return new MddExtFormulaRule();
    }


    @Bean
    @ConditionalOnMissingClass
    public UnstopBillRule unStopBillRule(){
        return new UnstopBillRule();
    }
}
