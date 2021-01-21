package com.pp.seckilltest.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author phf 2021/1/5 16:35
 */
@EnableCaching
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return new GenericJackson2JsonRedisSerializer(om);
    }

    /**
     * 配置缓存
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                               GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        // 设置缓存过期时间为5小时
                        .entryTtl(Duration.ofHours(10))
                        // 禁用缓存空值，不缓存null校验
                        .disableCachingNullValues()
                        // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                .fromSerializer(genericJackson2JsonRedisSerializer));
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>(16);
        //缓存键,且30秒后过期,30秒后再次调用方法时需要重新缓存
        configurationMap.put("test", cacheConfiguration.entryTtl(Duration.ofDays(180)));
        // 设置默认的cache组件
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration)
                .withInitialCacheConfigurations(configurationMap)
                .transactionAware()
                .build();
    }

}
