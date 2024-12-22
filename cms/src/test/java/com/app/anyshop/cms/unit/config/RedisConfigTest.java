/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.unit.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.anyshop.cms.config.RedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfigTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        redisConfig = new RedisConfig();
    }

    @Test
    void redisTemplate_setsKeyAndValueSerializersCorrectly() {
        RedisTemplate<String, String> result = redisConfig.redisTemplate(redisTemplate);

        verify(redisTemplate).setKeySerializer(any(StringRedisSerializer.class));
        verify(redisTemplate).setValueSerializer(any(StringRedisSerializer.class));
        assertNotNull(result);
    }

    @Test
    void getGson_returnsGsonInstance() {
        assertNotNull(redisConfig.getGson());
    }
}