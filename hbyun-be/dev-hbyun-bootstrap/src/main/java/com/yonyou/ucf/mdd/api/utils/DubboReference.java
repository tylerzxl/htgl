package com.yonyou.ucf.mdd.api.utils;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/10/28 0028 10:14
 */
public class DubboReference<T> extends com.yonyou.ucf.mdd.ext.dubbo.DubboReference<T> {

    private static volatile DubboReference instance;

    public static DubboReference getInstance() {
        if (instance == null) {
            synchronized (DubboReference.class) {
                if (instance == null) {
                    instance = new DubboReference();
                }
            }
        }
        return instance;
    }

    public T getReference(Class<T> clazz, String group, String version) {
        return super.getReference(clazz, group, version, null);
    }


    public T getReference(Class<T> clazz, String group, String version, Integer timeout) {
        return super.getReference(clazz, group, version, timeout);
    }
}
