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

import com.app.anyshop.cms.config.RequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequestFilterConfigTest {

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  private RequestFilter requestFilter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    requestFilter = new RequestFilter();
  }

  @Test
  void doFilterInternal_authenticatesSuccessfully() throws ServletException, IOException {
    when(request.getHeader("X-API-USER")).thenReturn("testUser");
    when(request.getHeader("X-API-USER-ROLE")).thenReturn("ROLE_USER");
    when(request.getHeader("X-API-USER-ID")).thenReturn("123");

    requestFilter.doFilterInternal(request, response, filterChain);

    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(authentication);
    assertEquals(new ImmutablePair<>("testUser", "123"), authentication.getPrincipal());
    assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
  }

  @Test
  void doFilterInternal_failsAuthenticationWhenHeadersMissing()
      throws ServletException, IOException {
    when(request.getHeader("X-API-USER")).thenReturn(null);
    when(request.getHeader("X-API-USER-ROLE")).thenReturn(null);
    when(request.getHeader("X-API-USER-ID")).thenReturn(null);

    requestFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_failsAuthenticationWhenSomeHeadersMissing()
      throws ServletException, IOException {
    when(request.getHeader("X-API-USER")).thenReturn("testUser");
    when(request.getHeader("X-API-USER-ROLE")).thenReturn(null);
    when(request.getHeader("X-API-USER-ID")).thenReturn("123");

    requestFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
