package com.yonyou.ucf.mdf.configuration;

import com.yonyou.common.bizflow.rule.BizFlowPushRule;
import com.yonyou.ucf.mdd.ext.bill.rule.social.SaveBankRule;
import com.yonyou.ucf.mdd.rules.*;
import com.yonyou.ucf.mdd.isv.rpc.rule.ISVSubmitBillRule;
import com.yonyou.ucf.mdd.isv.rpc.rule.ISVUnsubmitBillRule;
import com.yonyou.ucf.mdf.domain.rule.ExtBackEndRule;
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
public class MddRuleConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SaveBankRule basedocSaveBankRule() {
        return new SaveBankRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddDeleteRule mddDeleteRule() {
        return new MddDeleteRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddCheckUniqueRule mddCheckUniqueRule() {
        return new MddCheckUniqueRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddDetailRule mddDetailRule() {
        return new MddDetailRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddMovePrevRule mddMovePrevRule() {
        return new MddMovePrevRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddMoveFirstRule mddMoveFirstRule() {
        return new MddMoveFirstRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddMoveLastRule mddMoveLastRule() {
        return new MddMoveLastRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddMoveNextRule mddMoveNextRule() {
        return new MddMoveNextRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public MddQueryTreeRule mddQueryTreeRule() {
        return new MddQueryTreeRule();
    }

    @Bean
    @Primary
    @ConditionalOnMissingClass
    public ISVSubmitBillRule mddSubmitRule() {
        return new ISVSubmitBillRule();
    }

    @Bean
    @Primary
    @ConditionalOnMissingClass
    public ISVUnsubmitBillRule mddUnSubmitRule() {
        return new ISVUnsubmitBillRule();
    }

    @Bean
    @ConditionalOnMissingClass
    public BizFlowPushRule bizFlowPushRule(){
        return new BizFlowPushRule();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtBackEndRule extBackEndRule(){
        return new ExtBackEndRule();
    }
}
