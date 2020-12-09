package com.yonyou.ucf.mdf.app.extend.event;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.ISVEventListener;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.pojo.ISVEventType;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.network.cryptor.EncryptionHolder;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ISVDemoEventSTAFF_ADD implements ISVEventListener<ISVDemoEventClass> {

  @Override
  public boolean onEvent(ISVEventType type, ISVDemoEventClass event, EncryptionHolder holder) {
    String jsonString = JSON.toJSONString(event);
    if (StringUtils.isNotBlank(jsonString)){
      log.info("成功获取员工增加事件的请求参数{}",event);
      return true;
    }
    return false;
  }

  @Override
  public int priority() {
    return ISVEventListener.super.priority()-100;
  }

  @Override
  public Set<ISVEventType> supportTypes() {
    return Sets.newHashSet(ISVEventType.STAFF_ADD);
  }

  @Override
  public Class<ISVDemoEventClass> getEventClass() {
    return ISVDemoEventClass.class;
  }
}
