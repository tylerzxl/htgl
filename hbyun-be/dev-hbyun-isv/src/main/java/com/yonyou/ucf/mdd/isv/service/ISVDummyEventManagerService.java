package com.yonyou.ucf.mdd.isv.service;

import com.yonyou.diwork.exception.BusinessException;
import com.yonyou.iuap.event.rpc.IEventManageService;
import com.yonyou.iuap.event.vo.EventListenerVO;
import com.yonyou.iuap.event.vo.EventTypeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/9/16 21:10
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ISVDummyEventManagerService implements IEventManageService {

    @Override
    public void saveEventType(EventTypeVO eventTypeVO) {
        log.info("dummy save event type {}", eventTypeVO);
    }

    @Override
    public void createEventType(EventTypeVO eventTypeVO) {
        log.info("dummy create event type {}", eventTypeVO);
    }

    @Override
    public void saveEventListener(EventListenerVO eventListenerVO) throws BusinessException {
        log.info("dummy save event listener {}", eventListenerVO);
    }

    @Override
    public void createEventListener(EventListenerVO eventListenerVO) throws BusinessException {
        log.info("dummy create event listener {}", eventListenerVO);
    }

    @Override
    public EventTypeVO findEventType(String sourceId, String typeCode) {
        log.info("dummy create event type {} {}", sourceId, typeCode);
        return null;
    }
}
