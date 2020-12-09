package com.yonyou.ucf.mdd.isv.rpc.impl;

import com.yonyou.ucf.mdd.api.interfaces.rpc.IComOperateApi;
import com.yonyou.ucf.mdd.common.model.SavePoint;
import org.imeta.orm.base.BizObject;

import java.util.List;

/**
 * <p>Title</p>
 * <p>Description</p>
 *
 * @Author chouhl
 * @Date 2020-05-06$ 14:45$
 * @Version 1.0
 **/
public class ISVComOperateApiImpl implements IComOperateApi {

    @Override
    public <T extends BizObject> void insert(String fullname, T bill) {

    }

    @Override
    public <T extends BizObject> void insert(String fullname, List<T> bills) {

    }

    @Override
    public boolean commit(List<SavePoint> savePoints) {
        return false;
    }

    @Override
    public int executeSql(String statement, Object parameter) {
        return 0;
    }

    @Override
    public <T extends BizObject> void update(String fullname, T bill) {

    }

    @Override
    public <T extends BizObject> void update(String fullname, List<T> bills) {

    }

    @Override
    public void delete(String fullname, Long id) {

    }

    @Override
    public <T extends BizObject> void delete(String fullname, List<T> bills) {

    }

    @Override
    public boolean rollback(List<SavePoint> savePoints) {
        return false;
    }

}
