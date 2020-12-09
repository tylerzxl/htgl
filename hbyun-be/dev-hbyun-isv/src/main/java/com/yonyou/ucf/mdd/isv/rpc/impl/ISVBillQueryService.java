package com.yonyou.ucf.mdd.isv.rpc.impl;

import com.yonyou.ucf.mdd.api.interfaces.rpc.IComOperateApi;
import com.yonyou.ucf.mdd.api.interfaces.rpc.IComQueryApi;
import com.yonyou.ucf.mdd.common.model.uimeta.UIMetaBaseInfo;
import com.yonyoucloud.iuap.ucf.mdd.starter.core.module.beans.BeanUtils;
import com.yonyoucloud.uretail.api.IBillQueryService;
import com.yonyoucloud.uretail.bill.dto.BaseDto;
import com.yonyoucloud.uretail.bill.rule.common.RuleResult;
import com.yonyoucloud.uretail.model.BillContext;
import com.yonyoucloud.uretail.model.BillRuleRegister;
import com.yonyoucloud.uretail.report.model.StaticReport;
import com.yonyoucloud.uretail.ts.SavePoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imeta.orm.base.BizObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ISVBillQueryService implements IBillQueryService {

    private final IComQueryApi queryApi;

    private final IComOperateApi operateApi;

    @Override
    public Object findById(String s, Long aLong) throws Exception {
        return queryApi.findById(s, aLong);
    }

    @Override
    public List<Map<String, Object>> queryById(Long aLong, String s, String s1) throws Exception {
        return queryApi.queryById(aLong, s, s1);
    }

    @Override
    public List<Map<String, Object>> queryByIds(Long[] longs, String s, String s1) throws Exception {
        return queryApi.queryByIds(longs, s, s1);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) throws Exception {
        return queryApi.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, String s1, Object o) throws Exception {
        return queryApi.selectList(s, s1, o);
    }

    @Override
    public <E> List<E> selectSql(String s, Object o) throws Exception {
        return queryApi.selectSql(s, o);
    }

    @Override
    public <T extends Map<String, Object>> List<T> query(String s, String s1) throws Exception {
        return queryApi.query(s, s1);
    }

    @Override
    public <T extends Map<String, Object>> List<T> query(BillContext billContext, String s) throws Exception {
        UIMetaBaseInfo info = new UIMetaBaseInfo();
        BeanUtils.copyPropertiesIgnoresNull(billContext, info, s);
        return queryApi.query(info, s);
    }

    @Override
    public Map<String, Object> handleDataPermission(BillContext billContext, String s, String s1) throws Exception {
        UIMetaBaseInfo info = new UIMetaBaseInfo();
        BeanUtils.copyPropertiesIgnoresNull(billContext, info, s, s1);
        return queryApi.handleDataPermission(info, s, s1);
    }

    @Override
    public <T extends BizObject> void insert(String s, T strings) throws Exception {
        operateApi.insert(s, strings);
    }

    @Override
    public <T extends BizObject> void insert(String s, List<T> list) throws Exception {
        operateApi.insert(s, list);
    }

    @Override
    public void delete(String s, Long aLong) throws Exception {
        operateApi.delete(s, aLong);
    }

    @Override
    public <T extends BizObject> void delete(String s, List<T> list) throws Exception {
        operateApi.delete(s, list);
    }

    @Override
    public <T extends BizObject> void update(String s, T strings) throws Exception {
        operateApi.update(s, strings);
    }

    @Override
    public <T extends BizObject> void update(String s, List<T> list) throws Exception {
        operateApi.update(s, list);
    }

    @Override
    public boolean commit(List<SavePoint> list) throws Exception {
        List<com.yonyou.ucf.mdd.common.model.SavePoint> commSavePoints = new ArrayList<>();
        for (SavePoint savePoint : list) {
            com.yonyou.ucf.mdd.common.model.SavePoint commSavePoint = new com.yonyou.ucf.mdd.common.model.SavePoint();
            BeanUtils.copyPropertiesIgnoresNull(commSavePoint, savePoint);
            commSavePoints.add(commSavePoint);
        }
        return operateApi.commit(commSavePoints);
    }

    @Override
    public boolean rollback(List<SavePoint> list) throws Exception {
        List<com.yonyou.ucf.mdd.common.model.SavePoint> commSavePoints = new ArrayList<>();
        for (SavePoint savePoint : list) {
            com.yonyou.ucf.mdd.common.model.SavePoint commSavePoint = new com.yonyou.ucf.mdd.common.model.SavePoint();
            BeanUtils.copyPropertiesIgnoresNull(commSavePoint, savePoint);
            commSavePoints.add(commSavePoint);
        }
        return operateApi.rollback(commSavePoints);
    }

    @Override
    public int executeSql(String s, Object o) throws Exception {
        return operateApi.executeSql(s, o);
    }

    @Override
    public Map<String, Object> register(Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public String referenceCheck(String s, String s1, String s2) throws Exception {
        return queryApi.referenceCheck(s, s1, s2);
    }

    @Override
    public Object getRefData(BillContext billContext, Object o, Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public RuleResult executeRule(BillRuleRegister billRuleRegister, BillContext billContext, Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public RuleResult doAction(String s, BillRuleRegister billRuleRegister, BillContext billContext, Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public RuleResult executeRule(BillContext billContext, BaseDto baseDto) throws Exception {
        return null;
    }

    @Override
    public RuleResult pureExecuteRule(BillContext billContext, BaseDto baseDto) throws Exception {
        return null;
    }

    @Override
    public Object getOptionValueByName(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public List<Object> getOptionValueByNames(List<String> list, String s) throws Exception {
        return null;
    }

    @Override
    public StaticReport getStaticReport(Long aLong) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> getBillMeta(boolean b, boolean b1, Map<String, Object> map, boolean b2, boolean b3) throws Exception {
        return null;
    }

    @Override
    public boolean isItf(String s, String s1) throws Exception {
        return false;
    }

    @Override
    public String getFirstChildField(String s) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> openApp(Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public Boolean saveRegistInfo(Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public Object getSimpleVM(String s, Long aLong, Boolean aBoolean, Boolean aBoolean1) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> getBillRelationInfo(Map<String, String> map) throws Exception {
        return null;
    }

    @Override
    public String getThirdPathUrl(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public Object getGroupControls(String s, Long aLong, String s1, Object o, boolean b) throws Exception {
        return null;
    }

    @Override
    public void saveUserItemsSet(Object o, List<Map<String, Object>> list) throws Exception {

    }

    @Override
    public void restoreUserSet(Object o, String s, Long aLong, String s1) throws Exception {

    }

    @Override
    public BillContext getBillContext(String s) {
        return null;
    }

    @Override
    public Object getViewModel(String s, Map<String, Object> map) throws Exception {
        return null;
    }

    @Override
    public Object getMetaFilters(int i) {
        return null;
    }

    @Override
    public Object getFiltersInfo(int i) throws Exception {
        return null;
    }

    @Override
    public Object getSolutions(int i, int i1, String s) {
        return null;
    }

    @Override
    public Object getSolutionInfo(String s, int i) {
        return null;
    }

    @Override
    public Object getFilterQuick(int i, int i1) {
        return null;
    }

    @Override
    public Object getExtV4FilterQuick(int i, Object o) {
        return null;
    }

    @Override
    public Object getEntityAttr(String s, String s1) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> getMakeBillRuleList(String s) throws Exception {
        return null;
    }

    @Override
    public List<Map<String, Object>> getRuleBackSourceList(List<String> list) throws Exception {
        return null;
    }
}
