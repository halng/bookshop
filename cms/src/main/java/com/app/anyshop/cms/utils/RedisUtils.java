/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.utils;

import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {
  private final RedisTemplate<String, String> redisTemplate;
  private final Gson gson;

  public RedisUtils(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.gson = new Gson();
  }

  /**
   * Saves data to the cache with the specified key.
   *
   * @param key the key under which the data will be stored
   * @param value the data to be stored
   * @param <T> the type of the data
   */
  public <T> void saveDataToCache(String key, T value) {
    var valueOps = redisTemplate.opsForValue();
    String jsonData = gson.toJson(value);
    valueOps.set(key, jsonData);
  }

  /**
   * Saves data to the cache with the specified key and timeout.
   *
   * @param key the key under which the data will be stored
   * @param value the data to be stored
   * @param timeout the time in seconds after which the data will expire
   * @param <T> the type of the data
   */
  public <T> void saveDataToCache(String key, T value, long timeout) {
    var valueOps = redisTemplate.opsForValue();
    String jsonData = gson.toJson(value);
    Duration duration = Duration.ofSeconds(timeout);
    valueOps.set(key, jsonData, duration);
  }

  /**
   * Retrieves data from the cache with the specified key.
   *
   * @param key the key under which the data is stored
   * @param clazz the class of the data to be retrieved
   * @param <T> the type of the data
   * @return the data retrieved from the cache, or null if not found
   */
  public <T> T getDataFromCache(String key, Class<T> clazz) {
    var valueOps = redisTemplate.opsForValue();
    String jsonData = valueOps.get(key);
    return jsonData != null ? gson.fromJson(jsonData, clazz) : null;
  }

  /**
   * Retrieves data from the cache with the specified key.
   *
   * @param key the key under which the data is stored
   * @param type the type of the data to be retrieved (e.g. List<T>)
   * @param <T> the type of the data
   * @return the data retrieved from the cache, or null if not found
   */
  public <T> T getDataFromCache(String key, Type type) {
    var valueOps = redisTemplate.opsForValue();
    String jsonData = valueOps.get(key);
    return jsonData != null ? gson.fromJson(jsonData, type) : null;
  }
}
