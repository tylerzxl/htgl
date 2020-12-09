package org.imeta.spring.support.cache;

import com.yonyou.ucf.mdd.ext.core.AppContext;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imeta.core.lang.BooleanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 本类主要用于内部实现
 *
 * @author liuhaoi
 * @since Created At 2020/6/1 9:56 下午
 */
@Slf4j
public class MetaRedisManager implements DisposableBean {

    private static MetaRedisManager instance;
    private final RedisClientInterface metaRedisClient;
    private Integer defaultDuration = 60 * 60;
    private Integer  expireDuration = 7*24*60*60;


    private MetaRedisManager(RedisClientInterface metaRedisClient) {
        this.metaRedisClient = metaRedisClient;
    }

    public static MetaRedisManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (MetaRedisManager.class) {
            if (instance != null) {
                return instance;
            }
            RedisProperties redisProperties = AppContext.getBean(RedisProperties.class);
            RedisConfig redisConfig = buildRedisConfig(redisProperties);
            redisConfig.setManagerType("MetaRedisManager");
            RedisClientInterface metaRedisClient;
            if (redisConfig.getSentinel()) {
                metaRedisClient = new RedisSentinelClient(redisConfig);
            } else if (redisConfig.getCluster()) {
                metaRedisClient = new RedisClusterClient(redisConfig);
            } else {
                metaRedisClient = new RedisClient(redisConfig);
            }
            metaRedisClient.select(redisConfig.getIndex());
            instance = new MetaRedisManager(metaRedisClient);
        }
        return instance;
    }

    public static MetaRedisManager getInstance(int index) {
        MetaRedisManager metaRedisManager = getInstance();
        if (metaRedisManager == null) {
            return null;
        }
        return metaRedisManager.select(index);
    }

    private static RedisConfig buildRedisConfig(RedisProperties redisProperties) {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setServer(redisProperties.getHost());
        redisConfig.setPort(redisProperties.getPort());
        redisConfig.setPassword(redisProperties.getPassword());
        redisConfig.setTimeout((int) (redisProperties.getTimeout().toMillis()));
        redisConfig.setSsl(redisProperties.isSsl());
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        redisConfig.setSentinel(sentinel != null);
        redisConfig.setCluster(redisProperties.getCluster() != null);
        if (sentinel != null) {
            redisConfig.setMasterName(sentinel.getMaster());
        }
        redisConfig.setIndex(redisProperties.getDatabase());
        //return Integer.valueOf(String.valueOf(map.get(RedisConstants.REDIS_INDEX)));
        return redisConfig;
    }

    private static String getUrl() {
        return "/cache/config";
    }

    public static void main(String[] args) {
        MetaRedisManager metaRedisManager = MetaRedisManager.getInstance();
        metaRedisManager.set("12", "34");
        System.out.println(metaRedisManager.get("12"));
        MetaRedisManager metaRedisManager1 = MetaRedisManager.getInstance();
        System.out.println(metaRedisManager1.get("12"));
    }

    public Integer getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(Integer defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    @Override
    public void destroy() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("MetaRedisManager is destroying");
        }
        if (metaRedisClient != null) {
            metaRedisClient.stop();
        }
        instance = null;
        if (log.isDebugEnabled()) {
            log.debug("MetaRedisManager is destroyed");
        }
    }

    public boolean exists(String key) {
        return metaRedisClient.exists(key);
    }

    public int ttl(String key) {
        Long l = metaRedisClient.ttl(key);
        return (l != null && l > 0) ? l.intValue() : 0;
    }

    public <T> T getObject(String key) {
        try {
            return (T) metaRedisClient.getObject(key);
        } catch (IOException e) {
            throw new RuntimeException("redis_io", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("redis_serialize", e);
        }
    }

    public List<Object> getObjects(String... keys) {
        return metaRedisClient.getObjects(keys);
    }

    public String get(String key) {
        return metaRedisClient.get(key);
    }

    public byte[] get(byte[] key) {
        return metaRedisClient.get(key);
    }

    public List<String> mget(String... keys) {
        return metaRedisClient.mget(keys);
    }

    public boolean setObject(String key, Object value) {
        try {
            return metaRedisClient.setObject(key, value);
        } catch (IOException e) {
            throw new RuntimeException("redis_io", e);
        }
    }

    public boolean setObjects(Map<String, ?> keyValues) {
        try {
            return metaRedisClient.setObjects(keyValues);
        } catch (IOException e) {
            throw new RuntimeException("redis_io", e);
        }
    }

    public boolean setObject(String key, Object value, int duration) {
        if (duration == 0) {
            duration = defaultDuration;
        }
        try {
            return metaRedisClient.setObject(key, value, duration);
        } catch (IOException e) {
            throw new RuntimeException("redis_io", e);
        }
    }

    public boolean set(String key, String value) {
        return metaRedisClient.set(key, value);
    }

    public boolean set(byte[] key, byte[] value) {
        return metaRedisClient.set(key, value);
    }

    public boolean set(String key, String value, int duration) {
        if (duration == 0) {
            duration = defaultDuration;
        }
        return metaRedisClient.set(key, value, duration);
    }

    public byte[] getAndExpire(byte[] key, String tenantId){
        byte [] result = metaRedisClient.get(key);
        if(null != result && getIsExpire() && StringUtils.isNotEmpty(tenantId)){
            metaRedisClient.expire(key, expireDuration);
        }
        return result;
    }
    /**
     * 默认续期7*24*60*60
     * 通过 ttl 'key' 可查看缓存剩余时间
     * */
    public Boolean getIsExpire(){
        String isExpire = null;// PropertyUtil.getPropertyByKey(REDIS_EXPIRE_ENABLE);
        if (StringUtils.isEmpty(isExpire) && Objects.nonNull(UnifiedMetaProperties.getInstance())) {
            isExpire = UnifiedMetaProperties.getInstance().getRedisExpire();
        }
        return BooleanUtils.b(isExpire, true);
    }

    public boolean setnx(String key, String value, int duration) {
        if (duration == 0) {
            duration = defaultDuration;
        }
        return metaRedisClient.setnx(key, value, duration);
    }

    public boolean setxx(String key, String value, int duration) {
        if (duration == 0) {
            duration = defaultDuration;
        }
        return metaRedisClient.setxx(key, value, duration);
    }

    public boolean mset(String... keyValues) {
        return metaRedisClient.mset(keyValues);
    }

    public Long del(String... keys) {
        return metaRedisClient.del(keys);
    }

    public void batchDel(String key) {
        metaRedisClient.batchDel(key);
    }

    public Long incr(String key) {
        return metaRedisClient.incr(key);
    }

    public Long incrBy(String key, long by) {
        return metaRedisClient.incrBy(key, by);
    }

    public Long decr(String key) {
        return metaRedisClient.decr(key);
    }

    public Long decrBy(String key, long by) {
        return metaRedisClient.decrBy(key, by);
    }

    public Long lpush(String key, String... values) {
        return metaRedisClient.lpush(key, values);
    }

    public String rpop(String key) {
        return metaRedisClient.rpop(key);
    }

    public Long rpush(String key, String... values) {
        return metaRedisClient.rpush(key, values);
    }

    public String lpop(String key) {
        return metaRedisClient.lpop(key);
    }

    public String rpoplpush(String srckey, String dstkey) {
        return metaRedisClient.rpoplpush(srckey, dstkey);
    }

    public Long expire(String key, int duration) {
        return metaRedisClient.expire(key, duration);
    }

    public Long persist(String key) {
        return metaRedisClient.persist(key);
    }

    public Set<String> keys(String pattern) {
        return metaRedisClient.keys(pattern);
    }

    public String getSet(String key, String value) {
        return metaRedisClient.getSet(key, value);
    }

    public Long sadd(String key, String... members) {
        return metaRedisClient.sadd(key, members);
    }

    public Long srem(String key, String... members) {
        return metaRedisClient.srem(key, members);
    }

    public Long scard(String key) {
        return metaRedisClient.scard(key);
    }

    public boolean sismember(String key, String member) {
        return metaRedisClient.sismember(key, member);
    }

    public Set<String> smembers(String key) {
        return metaRedisClient.smembers(key);
    }

    public Set<String> sdiff(String... keys) {
        return metaRedisClient.sdiff(keys);
    }

    public Set<String> sunion(String... keys) {
        return metaRedisClient.sunion(keys);
    }

    public Set<String> sinter(String... keys) {
        return metaRedisClient.sinter(keys);
    }

    public Long hset(String key, String field, String value) {
        return metaRedisClient.hset(key, field, value);
    }

    public boolean hmset(String key, Map<String, String> map) {
        return metaRedisClient.hmset(key, map);
    }

    public Long hdel(String key, String... fields) {
        return metaRedisClient.hdel(key, fields);
    }

    public Long hlen(String key) {
        return metaRedisClient.hlen(key);
    }

    public boolean hexists(String key, String field) {
        return metaRedisClient.hexists(key, field);
    }

    public String hget(String key, String field) {
        return metaRedisClient.hget(key, field);
    }

    public List<String> hmget(String key, String... fields) {
        return metaRedisClient.hmget(key, fields);
    }

    public Set<String> hkeys(String key) {
        return metaRedisClient.hkeys(key);
    }

    public List<String> hvals(String key) {
        return metaRedisClient.hvals(key);
    }

    public Map<String, String> hgetAll(String key) {
        return metaRedisClient.hgetAll(key);
    }

    public Long hincrBy(String key, String field, long by) {
        return metaRedisClient.hincrBy(key, field, by);
    }

    public Long hincrBy(String key, String field) {
        return metaRedisClient.hincrBy(key, field);
    }

    public Long hdecrBy(String key, String field, long by) {
        return metaRedisClient.hdecrBy(key, field, by);
    }

    public Long hdecr(String key, String field) {
        return metaRedisClient.hdecr(key, field);
    }

    public Boolean getbit(String key, long offset) {
        return metaRedisClient.getbit(key, offset);
    }

    public Boolean setbit(String key, long offset, boolean value) {
        return metaRedisClient.setbit(key, offset, value);
    }

    public Long bitcount(String key) {
        return metaRedisClient.bitcount(key);
    }

    public Long bitcount(String key, long start, long end) {
        return metaRedisClient.bitcount(key, start, end);
    }

    public Long bitop(BitOP bitOP, String destKey, String... srcKeys) {
        return metaRedisClient.bitop(bitOP, destKey, srcKeys);
    }

    public Object eval(String script, LinkedHashMap<String, String> keyValues) {
        return metaRedisClient.eval(script, keyValues);
    }

    public void trans(TransHandler handler, Object obj) {
        metaRedisClient.trans(handler, obj);
    }

    public void trans(TransHandler handler) {
        metaRedisClient.trans(handler, null);
    }

    public void batch(BatchHandler handler, Object obj) {
        metaRedisClient.batch(handler, obj);
    }

    public void batch(BatchHandler handler) {
        metaRedisClient.batch(handler, null);
    }

    public MetaRedisManager select(int index) {
        metaRedisClient.select(index);
        return this;
    }

    public Jedis getJedisSession() {
        if (metaRedisClient instanceof JedisVistor) {
            JedisVistor vistor = (JedisVistor) metaRedisClient;
            return vistor.getJedisSession();
        }
        return null;//metaRedisClient.getJedisSession();
    }

    public Long publish(String channel, String msg) {
        return metaRedisClient.publish(channel, msg);
    }

}
