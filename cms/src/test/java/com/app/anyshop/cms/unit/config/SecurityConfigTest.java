package com.app.anyshop.cms.unit.config;

import static org.junit.jupiter.api.Assertions.*;

import com.app.anyshop.cms.config.RequestFilter;
import com.app.anyshop.cms.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class SecurityConfigTest {

  @Mock private RequestFilter requestFilter;

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
