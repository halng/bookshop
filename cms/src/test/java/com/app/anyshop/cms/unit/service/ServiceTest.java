package com.app.anyshop.cms.unit.service;

import com.app.anyshop.cms.services.impl.ServiceImpl;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
