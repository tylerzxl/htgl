package com.yonyou.ucf.mdf.app.partition;

import com.yonyou.ucf.mdd.common.interfaces.partition.IPartitionCallback;
import com.yonyou.ucf.mdf.app.util.ApplicationContextUtil;
import org.springframework.stereotype.Component;

@Component
public class PartitionCallBack implements IPartitionCallback {

    @Override
    public Object get(String key) throws Exception {
        return ApplicationContextUtil.getThreadContext(key);
    }
}
