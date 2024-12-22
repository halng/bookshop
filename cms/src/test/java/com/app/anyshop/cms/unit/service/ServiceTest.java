/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.app.anyshop.cms.services.impl.ServiceImpl;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class ServiceTest {
  @Test
  void testGetCurrentUser() {
    var authen =
        new AbstractAuthenticationToken(null) {
          @Override
          public Object getCredentials() {
            return null;
          }

          @Override
          public Object getPrincipal() {
            return ImmutablePair.of("username", "userId");
          }
        };

    SecurityContextHolder.getContext().setAuthentication(authen);

    assertEquals("userId", ServiceImpl.getCurrentUser());
  }
}
