/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.unit.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.anyshop.cms.utils.RedisUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisUtilsTest {

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private ValueOperations<String, String> valueOps;

  private RedisUtils redisUtils;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    redisUtils = new RedisUtils(redisTemplate);
  }

  @Test
  void saveDataToCache_savesDataCorrectly() {
    String key = "testKey";
    String value = "testValue";

    redisUtils.saveDataToCache(key, value);

    verify(valueOps).set(key, new Gson().toJson(value));
  }

  @Test
  void saveDataToCache_withTimeout_savesDataCorrectly() {
    String key = "testKey";
    String value = "testValue";
    long timeout = 60L;

    redisUtils.saveDataToCache(key, value, timeout);

    verify(valueOps).set(key, new Gson().toJson(value), Duration.ofSeconds(timeout));
  }

  @Test
  void getDataFromCache_retrievesDataCorrectly() {
    String key = "testKey";
    String value = "testValue";
    when(valueOps.get(key)).thenReturn(new Gson().toJson(value));

    String result = redisUtils.getDataFromCache(key, String.class);

    assertEquals(value, result);
  }

  @Test
  void getDataFromCache_returnsNullWhenDataNotFound() {
    String key = "testKey";
    when(valueOps.get(key)).thenReturn(null);

    String result = redisUtils.getDataFromCache(key, String.class);

    assertNull(result);
  }

  @Test
  void getDataFromCache_withType_retrievesDataCorrectly() {
    String key = "testKey";
    String value = "testValue";
    Type type = new TypeToken<String>() {}.getType();
    when(valueOps.get(key)).thenReturn(new Gson().toJson(value));

    String result = redisUtils.getDataFromCache(key, type);

    assertEquals(value, result);
  }

  @Test
  void getDataFromCache_withType_returnsNullWhenDataNotFound() {
    String key = "testKey";
    Type type = new TypeToken<String>() {}.getType();
    when(valueOps.get(key)).thenReturn(null);

    String result = redisUtils.getDataFromCache(key, type);

    assertNull(result);
  }
}
