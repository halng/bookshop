/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.unit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.app.anyshop.cms.utils.EntityAuditorAware;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class EntityAuditorAwareTest {
  private final EntityAuditorAware entityAuditorAware = new EntityAuditorAware();

  @Test
  void getCurrentAuditor_whenUnauthorized_shouldReturnDefault() {
    SecurityContextHolder.getContext().setAuthentication(null);

    assertEquals("SYSTEM", entityAuditorAware.getCurrentAuditor().get());
  }

  @Test
  void getCurrentAuditor_whenAuthorized_shouldReturnUserId() {
    Authentication authentication =
        new AbstractAuthenticationToken(null) {
          @Override
          public Object getCredentials() {
            return null;
          }

          @Override
          public Object getPrincipal() {
            return new ImmutablePair<>("username", "userId");
          }
        };

    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertEquals("userId", entityAuditorAware.getCurrentAuditor().get());
  }
}
