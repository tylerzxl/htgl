package com.yonyou.ucf.mdf.event;

import com.google.common.collect.Sets;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.ISVEventListener;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.pojo.ISVEvent;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.module.isv.event.pojo.ISVEventType;
import com.yonyoucloud.iuap.ucf.mdd.starter.ucg.openapi.network.cryptor.EncryptionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 本类主要用于
 *
 * @author liuhaoi
 * @since Created At 2020/6/2 4:41 下午
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketDispatchListener implements ISVEventListener<ISVEvent> {

    @Override
    public boolean onEvent(ISVEventType type, ISVEvent event, EncryptionHolder holder) {
        return true;
    }

    @Override
    public Set<ISVEventType> supportTypes() {
        return Sets.newHashSet(ISVEventType.values());
    }

    @Override
    public Class<ISVEvent> getEventClass() {
        return ISVEvent.class;
    }
}
