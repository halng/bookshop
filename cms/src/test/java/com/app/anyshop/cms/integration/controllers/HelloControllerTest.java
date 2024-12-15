package com.app.anyshop.cms.integration.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.app.anyshop.cms.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void testHelloEndpointWithUnauthorized() {
    ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/hello", String.class);
    assertEquals(403, response.getStatusCodeValue());
  }

  @Test
  void testHelloEndpointWithAuthorized() {
    var headers = getHeaders();
    HttpEntity<?> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response =
        restTemplate.exchange("/api/v1/hello", HttpMethod.GET, entity, String.class);
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Hello World!", response.getBody());
  }
}
