package com.mayur29.urlshortner.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private RedisTemplate<String,Object> redisTemplate;

    public RedisService(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void set(String key,Object value,Long ttl){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key,jsonString,ttl,TimeUnit.MINUTES);
    }

    public <T> T get(String key,Class<T> entityClass) {
        Object o = redisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(o.toString(),entityClass);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
