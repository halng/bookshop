/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  private final Gson gson = new Gson();

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedisTemplate<String, String> redisTemplate) {
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    return redisTemplate;
  }

  public Gson getGson() {
    return gson;
  }
}
