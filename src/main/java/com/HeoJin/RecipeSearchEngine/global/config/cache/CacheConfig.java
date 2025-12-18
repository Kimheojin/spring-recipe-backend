package com.HeoJin.RecipeSearchEngine.global.config.cache;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@Profile({"!test"})
public class CacheConfig {

    // redis 기본 적책 느낌
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                // 10qns
                // 길이마다 차등?
                .entryTtl(Duration.ofMinutes(10))
                // null 일 경우 저장하지 않음
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                // json 으로 처리 (직렬화)
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }

    // spring cache 가 redis 를 어떻게 할지 관한
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf,
                                          RedisCacheConfiguration config) {
        // 캐시별 TTL 다르게 주고 싶으면 여기서 withInitialCacheConfigurations 사용

        return RedisCacheManager.builder(cf)
                //트랜잭션 커밋 이후에 캐시 반영
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }
}
