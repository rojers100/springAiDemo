package org.example.springaidemo.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 1. 工作流程：
 *    - 第一次请求：执行方法，将结果存入缓存
 *    - 后续请求：直接从缓存返回，不查询数据库
 *    - 缓存的 key 是 token，value 是 UserModel 对象
 * 2. 性能优化：
 *    - 减少数据库查询
 *    - 提高响应速度
 *    - 降低系统负载
 * 3. 线程安全：
 *    - ConcurrentMapCacheManager 使用 ConcurrentHashMap
 *    - 支持并发访问
 *    - 适合多线程环境
 */
@Configuration
@EnableCaching
public class CacheConfig {

//    简单缓存
//    @Bean
//    public CacheManager cacheManager() {
//        return new ConcurrentMapCacheManager("userCache");
//    }

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(
                    name,
                    com.google.common.cache.CacheBuilder.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)  // 缓存过期时间30分钟
                        .maximumSize(100)                        // 最大缓存100条数据
                        .recordStats()                           // 开启统计功能
                        .build()
                        .asMap(),
                    false);
            }
        };
        
        cacheManager.setCacheNames(Arrays.asList("userCache"));
        return cacheManager;
    }
} 