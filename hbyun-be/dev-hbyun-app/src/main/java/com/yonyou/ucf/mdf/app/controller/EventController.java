package com.yonyou.ucf.mdf.app.controller;

import com.yonyou.ucf.mdf.app.model.MetaInfoDto;
import com.yonyou.ucf.mdf.app.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * <p>Title: EventController</p>
 * <p>Description: 事件中心监听控制类</p>
 *
 * @author zhanghbs
 * @date 2020-04-16 20:15
 * @Version 1.0
 */
@Controller
@RequestMapping("/event/")
public class EventController {
      @Autowired
      private IEventService iEventService;
   /**
   * @Author zhanghbs
   * @Description //元数据中心数据更新和删除接收消息
   * @Date 2020/4/16 20:16
   * @Param  * @param ： request
   * @Return java.lang.Object
   **/
    @ResponseBody
    @RequestMapping(value = "/metaInfos")
    public Object updateMetaInfos(@RequestBody MetaInfoDto metaInfoDto) {
       return iEventService.updateMetaInfos(metaInfoDto);
    }

}
