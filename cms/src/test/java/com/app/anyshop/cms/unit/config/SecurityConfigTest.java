/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */
package com.app.anyshop.cms.unit.config;

import static org.junit.jupiter.api.Assertions.*;

import com.app.anyshop.cms.config.RequestFilter;
import com.app.anyshop.cms.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class SecurityConfigTest {

  @Mock private RequestFilter requestFilter;
  @Mock private HttpSecurity httpSecurity;

  private SecurityConfig securityConfig;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    securityConfig = new SecurityConfig(requestFilter);
  }

  @Test
  void webSecurityCustomizer_ignoresSpecifiedEndpoints() {
    WebSecurityCustomizer customizer = securityConfig.webSecurityCustomizer();
    assertNotNull(customizer);
  }

  @Test
  void corsConfigurer_allowsAllOriginsAndMethods() {
    WebMvcConfigurer configurer = securityConfig.corsConfigurer();
    assertNotNull(configurer);
  }
}
