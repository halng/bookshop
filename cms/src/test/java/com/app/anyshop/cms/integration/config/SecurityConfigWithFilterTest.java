/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */
package com.app.anyshop.cms.integration.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.app.anyshop.cms.integration.BaseIntegrationTest;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigWithFilterTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void testPublicEndpoints() throws Exception {
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

    mockMvc
        .perform(get("/api-docs/**"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  void testUnauthenticatedAccessToProtectedEndpoint() throws Exception {
    mockMvc
        .perform(get("/some-protected-endpoint"))
        .andExpect(MockMvcResultMatchers.status().is4xxClientError());
  }

  @Test
  void testAuthenticatedAccessToProtectedEndpoint() throws Exception {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "user", null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
    mockMvc
        .perform(
            get("/api/v1/hello")
                .header("X-API-USER-ID", "userid")
                .header("X-API-USER", "user")
                .header("X-API-USER-ROLE", "role"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
