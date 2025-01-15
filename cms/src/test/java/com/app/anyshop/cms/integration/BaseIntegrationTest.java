/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseIntegrationTest {
  protected final ObjectMapper objectMapper = new ObjectMapper();
  protected static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
          .withDatabaseName("cms")
          .withUsername("cms")
          .withPassword("cms");

  private static final GenericContainer<?> redisContainer =
      new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);

  static {
    postgreSQLContainer.start();
    redisContainer.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

    registry.add("spring.data.redis.host", redisContainer::getHost);
    registry.add("spring.data.redis.port", () -> 6379);

    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  protected HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-API-USER-ID", "userid");
    headers.add("X-API-USER", "user");
    headers.add("X-API-USER-ROLE", "role");
    return headers;
  }
}
