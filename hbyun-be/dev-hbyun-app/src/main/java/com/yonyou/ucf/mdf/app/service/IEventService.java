package com.yonyou.ucf.mdf.app.service;

import com.yonyou.ucf.mdf.app.model.MetaInfoDto;


/**
 * <p>Title: IEventService</p>
 * <p>Description: 事件中心服务接口</p>
 *
 * @author zhanghbs
 * @date 2020-04-21 20:10
 * @Version 1.0
 */
public interface IEventService {

    public Object updateMetaInfos(MetaInfoDto metaInfoDto);
}
