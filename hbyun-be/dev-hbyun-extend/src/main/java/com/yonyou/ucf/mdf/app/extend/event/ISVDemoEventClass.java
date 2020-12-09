package com.yonyou.ucf.mdf.app.extend.event;

import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.pojo.ISVEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ISVDemoEventClass extends ISVEvent {

  private String definitionVariable;

}
