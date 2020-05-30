package com.yanlaoge.gulimall.product.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * springCache 自定义配置
 * 配置内容:
 *    1. key序列化发放
 *    2. value序列化方式
 *    3. 因为使用自定义配置,需要导入 配置文件里的配置
 *      3.1  @EnableConfigurationProperties(CacheProperties.class)
 *      3.2 引入
 *        3.2.1 @Resource 注入
 *        3.2.2 方法注入
 *
 * @author rubyle
 */
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置文件的内容就不会用上
     *
     * @return RedisCacheConfiguration
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer( )));
        //获取配置文件配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if(redisProperties.getTimeToLive() != null){
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if(redisProperties.getKeyPrefix() != null){
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if(redisProperties.isCacheNullValues()){
            config = config.disableCachingNullValues();
        }
        if(!redisProperties.isUseKeyPrefix()){
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
