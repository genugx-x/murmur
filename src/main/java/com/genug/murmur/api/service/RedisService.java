package com.genug.murmur.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, String value, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS);
    }

    public void delete(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }

}
