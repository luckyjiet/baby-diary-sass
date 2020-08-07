package fun.littlecc.common.util;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @date 2019-07-29 14:11
 */
@Component("redisUtil")
public class RedisUtil {
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 通过执行lua脚本确定原子性（应对并发场景）
     *
     * @param script
     * @param keys
     * @return
     */
    public Object executeLua(String script, List<String> keys, Object... args) {
        DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Object.class);
        Object execute = redisTemplate.execute(redisScript,
                redisTemplate.getValueSerializer(),
                redisTemplate.getValueSerializer(),
                keys,
                args
        );
        return execute;
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if (result == null) {
            return null;
        }
        return result;
    }

    /**
     * 自增
     *
     * @param key
     * @param val
     * @return
     */
    public Object increment(final String key, double val) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.increment(key, val);
        if (result == null) {
            return null;
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入set集合缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sAdd(final String key, Object value) {
        boolean result = false;
        try {
            SetOperations operations = redisTemplate.opsForSet();
            operations.add(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入set集合缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sRemove(final String key, Object... value) {
        boolean result = false;
        try {
            SetOperations operations = redisTemplate.opsForSet();
            operations.remove(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取set 集合的member
     *
     * @param key
     * @return
     */
    public Object sMember(final String key) {
        Object result = null;
        try {
            SetOperations operations = redisTemplate.opsForSet();
            result = operations.members(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime SECONDS
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean hmset(String key, Map<String, String> value) {
        boolean result = false;
        try {
            redisTemplate.opsForHash().putAll(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, String> hmget(String key) {
        Map<String, String> result = null;
        try {
            result = redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 设置hash的值
     *
     * @param hKey  大key
     * @param key   小key
     * @param value 值
     * @return
     */
    public void setHashValue(String hKey, String key, Object value) {
        value = JSONUtil.toJsonStr(value);
        redisTemplate.opsForHash().put(hKey, key, value);
    }


    /**
     * 获取hash值
     *
     * @param hKey   大key
     * @param key    小key
     * @param tClass 泛型
     * @return
     */
    public <T> T getHashValue(String hKey, String key, Class<T> tClass) {
        Object o = redisTemplate.opsForHash().get(hKey, key);
        if (o == null) {
            return null;
        }
        return JSONUtil.toBean(o + "", tClass);
    }

    /**
     * 获取超时时间
     *
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /*********************************************** Sorted SET 相关操作 ***********************************************/

    /**
     * 实现命令 : ZADD key score member
     * 添加一个 成员/分数 对
     *
     * @param key
     * @param value 成员
     * @param score 分数
     * @return
     */
    public boolean zAdd(String key, double score, Object value) {
        Boolean result = redisTemplate.opsForZSet().add(key, value, score);
        if (result == null) {
            return false;
        }
        return result;
    }


    /**
     * 实现命令 : ZREM key member [member ...]
     * 删除成员
     *
     * @param key
     * @param values
     * @return
     */
    public Long zRem(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }


    /**
     * 实现命令 : ZREMRANGEBYRANK key start stop
     * 删除 start下标 和 end下标间的所有成员
     * 下标从0开始，支持负下标，-1表示最右端成员，包括开始下标也包括结束下标 (未验证)
     *
     * @param key
     * @param start 开始下标
     * @param end   结束下标
     * @return
     */
    public Long zRemRangeByRank(String key, int start, int end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }


    /**
     * 实现命令 : ZREMRANGEBYSCORE key start stop
     * 删除分数段内的所有成员
     * 包括min也包括max (未验证)
     *
     * @param key
     * @param min 小分数
     * @param max 大分数
     * @return
     */
    public Long zRemRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }


    /**
     * 实现命令 : ZSCORE key member
     * 获取成员的分数
     *
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }


    /**
     * 实现命令 : ZINCRBY key 带符号的双精度浮点数 member
     * 增减成员的分数
     *
     * @param key
     * @param value
     * @param delta 带符号的双精度浮点数
     * @return
     */
    public Double zInCrBy(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }


    /**
     * 实现命令 : ZCARD key
     * 获取集合中成员的个数
     *
     * @param key
     * @return
     */
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().size(key);
    }


    /**
     * 实现命令 : ZCOUNT key min max
     * 获取某个分数范围内的成员个数，包括min也包括max (未验证)
     *
     * @param key
     * @param min 小分数
     * @param max 大分数
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }


    /**
     * 实现命令 : ZRANK key member
     * 按分数从小到大获取成员在有序集合中的排名
     *
     * @param key
     * @param value
     * @return
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }


    /**
     * 实现命令 : ZREVRANK key member
     * 按分数从大到小获取成员在有序集合中的排名
     *
     * @param key
     * @param value
     * @return
     */
    public Long zRevRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }


    /**
     * 实现命令 : ZRANGE key start end
     * 获取 start下标到 end下标之间到成员，并按分数从小到大返回
     * 下标从0开始，支持负下标，-1表示最后一个成员，包括开始下标，也包括结束下标(未验证)
     *
     * @param key
     * @param start 开始下标
     * @param end   结束下标
     * @return
     */
    public Set<Object> zRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }


    /**
     * 实现命令 : ZREVRANGE key start end
     * 获取 start下标到 end下标之间到成员，并按分数从大到小返回
     * 下标从0开始，支持负下标，-1表示最后一个成员，包括开始下标，也包括结束下标(未验证)
     *
     * @param key
     * @param start 开始下标
     * @param end   结束下标
     * @return
     */
    public Set<Object> zRevRange(String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }


    /**
     * 实现命令 : ZRANGEBYSCORE key min max
     * 获取分数范围内的成员并按从小到大返回
     * (未验证)
     *
     * @param key
     * @param min 小分数
     * @param max 大分数
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }


    /**
     * 实现命令 : ZRANGEBYSCORE key min max LIMIT offset count
     * 分页获取分数范围内的成员并按从小到大返回
     * 包括min也包括max(未验证)
     *
     * @param key
     * @param min    小分数
     * @param max    大分数
     * @param offset 开始下标，下标从0开始
     * @param count  取多少条
     * @return
     */
    public Set<Object> zRangeByScore(String key, double min, double max, int offset, int count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }


    /**
     * 实现命令 : ZREVRANGEBYSCORE key min max
     * 获取分数范围内的成员并按从大到小返回
     * (未验证)
     *
     * @param key
     * @param min 小分数
     * @param max 大分数
     * @return
     */
    public Set<Object> zRevRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }


    /**
     * 实现命令 : ZREVRANGEBYSCORE key min max LIMIT offset count
     * 分页获取分数范围内的成员并按从大到小返回
     * 包括min也包括max(未验证)
     *
     * @param key
     * @param min    小分数
     * @param max    大分数
     * @param offset 开始下标，下标从0开始
     * @param count  取多少条
     * @return
     */
    public Set<Object> zRevRangeByScore(String key, double min, double max, int offset, int count) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, offset, count);
    }

    /*********************************************** Sorted SET 相关操作 ***********************************************/
}
