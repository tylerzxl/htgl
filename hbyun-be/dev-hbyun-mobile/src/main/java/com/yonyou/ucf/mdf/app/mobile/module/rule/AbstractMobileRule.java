package com.yonyou.ucf.mdf.app.mobile.module.rule;

import com.yonyou.ucf.mdd.rule.base.AbstractRule;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/28 7:42 下午
 */
public abstract class AbstractMobileRule extends AbstractRule {

    private static final String MOBILE_BILL_NUM_SPLIT = "Mobile";

    public boolean isMobileBillNum(String billNum) {
        //兼容处理移动BillNum和PC导致的billcode不同的问题
        return billNum != null && billNum.contains(MOBILE_BILL_NUM_SPLIT);
    }

    public String findUnifyBillNum(String billNum) {
        return billNum.split(MOBILE_BILL_NUM_SPLIT)[0];
    }


}
