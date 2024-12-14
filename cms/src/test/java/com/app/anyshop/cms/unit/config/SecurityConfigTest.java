package com.app.anyshop.cms.unit.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.app.anyshop.cms.config.RequestFilter;
import com.app.anyshop.cms.config.SecurityConfig;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

@Disabled("Skipping all tests in this class for now")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigWithFilterTest {

  @Autowired private MockMvc mockMvc;

  //  @Mock
  //  private MyRequestFilter requestFilter; // Mock the request filter if needed

  @Test
  void testPublicEndpoints() throws Exception {
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

    // {"type":"about:blank","title":"Not Found","status":404,"detail":"No static resource
    // api-docs/**.","instance":"/api-docs/**"}
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
