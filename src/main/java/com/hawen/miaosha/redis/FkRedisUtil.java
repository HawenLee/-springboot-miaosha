package com.hawen.miaosha.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Component
public class FkRedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public FkRedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        String realKey = prefix.getPrefix() + key;
        String str = redisTemplate.opsForValue().get(realKey);
        try {
            return stringToBean(str, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 添加key-value对
    public <T> Boolean set(KeyPrefix prefix, String key, T value) {
        String str = null;
        try {
            // 将T对象序列化为字符串
            str = beanToString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (str == null || str.length() <= 0) {
            return false;
        }
        // 实际的key由prefix和key组成，且prefix还决定key的过期时间
        String realKey = prefix.getPrefix() + key;
        // 获取过期时间
        int seconds = prefix.expireSeconds();
        // expireSeconds为过期时间，seconds <= 0代表永不过期
        if (seconds <= 0) {
            // 此处向Redis中添加普通key，value就是字符串
            // 不设置过期时间，就是永不过期
            redisTemplate.opsForValue().set(realKey, str);
        } else {
            // 最后一个参数设置过期时间，此处的过期事件以秒为单位
            redisTemplate.opsForValue().set(realKey, str,
                    Duration.ofSeconds(seconds));
        }
        return true;
    }

    // 判断指定key是否存在
    public Boolean exists(KeyPrefix prefix, String key) {
        String realPrefix = prefix.getPrefix() + key;
        return redisTemplate.hasKey(realPrefix);
    }

    // 根据key删除数据
    public Boolean delete(KeyPrefix prefix, String key) {
        String realPrefix = prefix.getPrefix() + key;
        // 删除指定key及对应的数据
        return redisTemplate.delete(realPrefix);
    }

    // 对指定key的值加一
    public Long incr(KeyPrefix prefix, String key) {
        String realPrefix = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().increment(realPrefix);
    }

    // 对指定key的值减一
    public Long decr(KeyPrefix prefix, String key) {
        String realPrefix = prefix.getPrefix() + key;
        return redisTemplate.opsForValue().decrement(realPrefix);
    }

    // 将对象转成JSON字符串
    public static <T> String beanToString(T value) throws JsonProcessingException {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        // 如果要转换的对象是整型，通过添加空字符串将其转成字符串
        if (clazz == Integer.class || clazz == int.class) {
            return "" + value;
        } else if (Long.class == clazz || clazz == long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            // 使用Jackson将对象转换成JSON字符串
            return objectMapper.writeValueAsString(value);
        }
    }

    public static <T> T stringToBean(String str, Class<T> clazz) throws JsonProcessingException {
        if (StringUtils.isBlank(str) || Objects.isNull(clazz)) {
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else {
            return objectMapper.readValue(str, clazz);
        }
    }


}
