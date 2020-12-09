package com.yonyou.ucf.mdf.app.service.impl;


import com.yonyou.ucf.mdf.app.model.MetaInfoDto;
import com.yonyou.ucf.mdf.app.service.IEventService;
import org.imeta.core.cache.CacheEvictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//import static org.imeta.core.listener.ClientListener.DEPENDENCY;
//import static org.imeta.core.listener.ClientListener.UNIFIED_TEMP;

/**
 * <p>Title: EventServiceImpl</p>
 * <p>Description: 事件中心接口服务实现</p>
 *
 * @author zhanghbs
 * @date 2020-04-21 20:11
 * @Version 1.0
 */
@Service("EventServiceImpl")
public class EventServiceImpl  implements IEventService {
    private static final String UNIFIED_TEMP = "unified_temp";
    private static final String DEPENDENCY = "dependency";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Object updateMetaInfos(MetaInfoDto metaInfoDto) {
        String sourceID = metaInfoDto.getSourceID();
        String eventType = metaInfoDto.getEventType();
        /**
         * 业务处理
         */
        if("METACENTER".equals(sourceID)&&"META_ALTER".equals(eventType)){
            List<String> ids = metaInfoDto.getUserObject();
            String type = metaInfoDto.getType();
            String tenantId=metaInfoDto.getTenantId();
            if (Objects.isNull(tenantId)) {
                CacheEvictService.removeAllCache();
                removeMultiKeysById(type,ids);

            } else {
                removeMultiKeysByIdAndTenantId(type, tenantId, ids);
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        return response;
    }
    private void removeMultiKeysByIdAndTenantId(String type, String tenantId, List<String> ids) {
        if (org.apache.commons.lang3.StringUtils.isAnyEmpty(type, tenantId)) {
            return;
        }

        ids.forEach(id -> {
            CacheEvictService.removeCacheByKey(getTempCacheKey(type, id, tenantId));
            CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency(type, id, tenantId));
            //删除redis缓存
            redisTemplate.delete(getTempCacheKey(type, id, tenantId));
            redisTemplate.delete(getTemCacheKeyWithDependency(type, id, tenantId));
        });
    }
    private void removeMultiKeysById(String type, List<String> ids) {
        if (org.apache.commons.lang3.StringUtils.isAnyEmpty(type)) {
            ids.forEach(id -> {

                CacheEvictService.removeCacheByKey(getTempCacheKey("entity", id));
                CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency("entity", id));
                //删除redis缓存
                redisTemplate.delete(getTempCacheKey("entity", id));
                redisTemplate.delete(getTemCacheKeyWithDependency("entity", id));

                CacheEvictService.removeCacheByKey(getTempCacheKey("component", id));
                CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency("component", id));
                //删除redis缓存
                redisTemplate.delete(getTempCacheKey("component", id));
                redisTemplate.delete(getTemCacheKeyWithDependency("component", id));

                CacheEvictService.removeCacheByKey(getTempCacheKey("interface", id));
                CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency("interface", id));
                //删除redis缓存
                redisTemplate.delete(getTempCacheKey("interface", id));
                redisTemplate.delete(getTemCacheKeyWithDependency("interface", id));

                CacheEvictService.removeCacheByKey(getTempCacheKey("enumeration", id));
                CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency("enumeration", id));
                //删除redis缓存
                redisTemplate.delete(getTempCacheKey("enumeration", id));
                redisTemplate.delete(getTemCacheKeyWithDependency("enumeration", id));

            });

        }

        ids.forEach(id -> {
            CacheEvictService.removeCacheByKey(getTempCacheKey(type, id));
            CacheEvictService.removeCacheByKey(getTemCacheKeyWithDependency(type, id));
            //删除redis缓存
            redisTemplate.delete(getTempCacheKey(type, id));
            redisTemplate.delete(getTemCacheKeyWithDependency(type, id));
        });
    }
    private String getTempCacheKey(String type, String id) {
        return String.format("%s:%s:%s", UNIFIED_TEMP, type, id);
    }

    private String getTempCacheKey(String type, String id, String tenantId) {
        return String.format("%s:%s:%s:%s", UNIFIED_TEMP, type, tenantId, id);
    }

    private String getTemCacheKeyWithDependency(String type, String id) {
        return String.format("%s:%s:%s:%s", UNIFIED_TEMP, type, id, DEPENDENCY);
    }

    private String getTemCacheKeyWithDependency(String type, String id, String tenantId) {
        return String.format("%s:%s:%s:%s:%s", UNIFIED_TEMP, type, tenantId, id, DEPENDENCY);
    }
}
