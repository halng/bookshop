/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.integration.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.app.anyshop.cms.constant.Message;
import com.app.anyshop.cms.dto.CreateCategoryVM;
import com.app.anyshop.cms.dto.DetailProductCategoryResVM;
import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import com.app.anyshop.cms.entity.ProductCategory;
import com.app.anyshop.cms.entity.Status;
import com.app.anyshop.cms.integration.BaseIntegrationTest;
import com.app.anyshop.cms.repositories.IProductCategoryRepo;
import com.app.anyshop.cms.utils.RedisUtils;
import java.net.URI;
import java.util.Collections;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.util.UriComponentsBuilder;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest extends BaseIntegrationTest {

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private IProductCategoryRepo productCategoryRepo;
  @Autowired private RedisUtils redisUtils;

  @BeforeEach
  void initializeDatabase() {
    productCategoryRepo.save(
        ProductCategory.builder()
            .name("category1")
            .description("category1 description")
            .status(Status.REJECTED)
            .build());
    productCategoryRepo.save(
        ProductCategory.builder()
            .name("category2")
            .description("category2 description")
            .status(Status.WAITING_APPROVAL)
            .build());
    productCategoryRepo.save(
        ProductCategory.builder()
            .name("category3")
            .description("category3 description")
            .status(Status.APPROVED)
            .build());

    restTemplate
        .getRestTemplate()
        .setInterceptors(
            Collections.singletonList(
                (request, body, execution) -> {
                  request.getHeaders().add("X-API-USER-ID", "userid");
                  request.getHeaders().add("X-API-USER", "user");
                  request.getHeaders().add("X-API-USER-ROLE", "super_admin");
                  return execution.execute(request, body);
                }));
  }

  @AfterEach
  void cleanDatabase() {
    productCategoryRepo.deleteAll();
  }

  @Test
  void testCreateCategory_whenRequestFulfill_ShouldSuccess() {
    CreateCategoryVM createCategoryVM = new CreateCategoryVM("category4", "category4 description");

    ResponseEntity<?> response =
        restTemplate.postForEntity("/api/v1/categories", createCategoryVM, Object.class);

    // assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.CREATED, responseBody.code());
    assertEquals(Message.CREATED_WAITING_APPROVAL, responseBody.msg());

    // validate data in database
    var categoryOp = productCategoryRepo.findByName("category4");
    if (categoryOp.isPresent()) {
      var category = categoryOp.get();
      assertEquals("category4", category.getName());
      assertEquals("category4 description", category.getDescription());
      assertEquals(Status.WAITING_APPROVAL, category.getStatus());
    } else {
      Assertions.fail("Category not found in database");
    }
  }

  @Test
  void testCreateCategory_whenRequestNotFulfill_ShouldError() {
    // arrange
    CreateCategoryVM createCategoryVM = new CreateCategoryVM(null, null);

    HttpEntity<?> entity = new HttpEntity<>(createCategoryVM);
    ResponseEntity<?> response =
        restTemplate.postForEntity("/api/v1/categories", entity, Object.class);

    // assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetAllCategories_whenPageIsValid_ShouldSuccess() {
    URI uri =
        UriComponentsBuilder.fromPath("/api/v1/categories").queryParam("page", 1).build().toUri();
    ResponseEntity<?> response = restTemplate.getForEntity(uri, Object.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    PagingResVM<?> responseBody =
        this.objectMapper.convertValue(response.getBody(), PagingResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.OK, responseBody.code());
  }

  @Test
  void testGetAllCategories_whenPageIsInvalid_ShouldReturnBadRequest() {
    URI uri =
        UriComponentsBuilder.fromPath("/api/v1/categories").queryParam("page", 0).build().toUri();
    ResponseEntity<?> response = restTemplate.getForEntity(uri, Object.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testUpdateCategory_whenStatusWaitingForApproval_ShouldSaveDirectlyToDB_Success() {
    // prepare data
    var cateOp = productCategoryRepo.findByName("category2");
    if (cateOp.isEmpty()) {
      Assertions.fail("Category not found in database");
    }
    var category = cateOp.get();

    // update category
    CreateCategoryVM updateCategoryVM =
        new CreateCategoryVM("category2-updated", "updated description");

    HttpEntity<?> entity = new HttpEntity<>(updateCategoryVM);
    ResponseEntity<?> response =
        restTemplate.exchange(
            "/api/v1/categories/" + category.getId(), HttpMethod.PUT, entity, Object.class);

    // assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.OK, responseBody.code());

    // validate data in database
    var updatedCategoryOp = productCategoryRepo.findById(category.getId());
    if (updatedCategoryOp.isPresent()) {
      var updatedCategory = updatedCategoryOp.get();
      assertEquals("category2-updated", updatedCategory.getName());
      assertEquals("updated description", updatedCategory.getDescription());
      assertEquals(Status.WAITING_APPROVAL, updatedCategory.getStatus());
    } else {
      Assertions.fail("Category not found in database");
    }
  }

  @Test
  void updateCategory_whenStatusREJECTED_ShouldReceiveBadRequest() {
    var cateOp = productCategoryRepo.findByName("category1");
    if (cateOp.isEmpty()) {
      Assertions.fail("Category not found in database");
    }
    var category = cateOp.get();

    CreateCategoryVM updateCategoryVM =
        new CreateCategoryVM("category1-updated", "updated description");
    ResponseEntity<?> response =
        restTemplate.exchange(
            "/api/v1/categories/" + category.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(updateCategoryVM),
            Object.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.BAD_REQUEST, responseBody.code());
    assertEquals(
        Message.CATEGORY_CAN_NOT_UPDATE.formatted(category.getName(), category.getStatus()),
        responseBody.msg());
  }

  @Test
  @Disabled("This test is disabled because now can not connect with redis")
  void updateCategory_whenStatusAPPROVED_ShouldSaveToRedis() {
    var cateOp = productCategoryRepo.findByName("category3");
    if (cateOp.isEmpty()) {
      Assertions.fail("Category not found in database");
    }
    var category = cateOp.get();

    CreateCategoryVM updateCategoryVM =
        new CreateCategoryVM("category3-updated", "updated description");
    URI uri = URI.create("/api/v1/categories/" + category.getId());
    ResponseEntity<?> response =
        restTemplate.exchange(
            uri, HttpMethod.PUT, new HttpEntity<>(updateCategoryVM), Object.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.OK, responseBody.code());
    assertEquals(Message.UPDATED_WAITING_APPROVAL, responseBody.msg());

    // assert data in database
    var updatedCategoryOp = productCategoryRepo.findById(category.getId());
    if (updatedCategoryOp.isPresent()) {
      var updatedCategory = updatedCategoryOp.get();
      assertEquals("category3", updatedCategory.getName());
      assertEquals("category3 description", updatedCategory.getDescription());
      assertEquals(Status.APPROVED, updatedCategory.getStatus());
    } else {
      Assertions.fail("Category not found in database");
    }

    // assert data in redis
    var key = Message.REDIS_KEY_UPDATE_CATEGORY_TEMPLATE.formatted("userid", category.getId());
    var redisData = redisUtils.getDataFromCache(key, CreateCategoryVM.class);
    assertEquals("category3-updated", redisData.name());
    assertEquals("updated description", redisData.description());
  }

  @Test
  void testUpdateCategory_whenRequestInvalid_ShouldError() {
    var header = getHeaders();
    CreateCategoryVM updateCategoryVM = new CreateCategoryVM(null, "");

    HttpEntity<?> entity = new HttpEntity<>(updateCategoryVM, header);
    ResponseEntity<?> response =
        restTemplate.exchange("/api/v1/categories/1", HttpMethod.PUT, entity, Object.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testUpdateStatusCategory_whenUserDontHavePermission_shouldError() {
    restTemplate
        .getRestTemplate()
        .setInterceptors(
            Collections.singletonList(
                (request, body, execution) -> {
                  request.getHeaders().add("X-API-USER-ID", "userid");
                  request.getHeaders().add("X-API-USER", "user");
                  request.getHeaders().add("X-API-USER-ROLE", "invalid-role");
                  return execution.execute(request, body);
                }));

    ResponseEntity<?> response =
        restTemplate.exchange(
            "/api/v1/categories/status/1?action=activate", HttpMethod.PUT, null, Object.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testUpdateStatusCategory_whenActionIsInvalid_ShouldError() {
    ResponseEntity<?> response =
        restTemplate.exchange(
            "/api/v1/categories/status/1?action=invalid",
            HttpMethod.PUT,
            new HttpEntity<>(null),
            Object.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetCategoryDetails_whenIdExists_ShouldSuccess() {
    var cateOp = productCategoryRepo.findByName("category1");
    if (cateOp.isEmpty()) {
      Assertions.fail("Category not found in database");
    }
    var category = cateOp.get();
    ResponseEntity<?> response =
        restTemplate.getForEntity(
            "/api/v1/categories/%s/details".formatted(category.getId()), Object.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.OK, responseBody.code());
    assertEquals(Message.SUCCESS, responseBody.msg());
    DetailProductCategoryResVM data =
        this.objectMapper.convertValue(responseBody.data(), DetailProductCategoryResVM.class);
    assertNotNull(data);
    assertEquals(category.getId(), data.id());
    assertEquals(category.getName(), data.name());
    assertEquals(category.getDescription(), data.description());
    assertEquals(category.getStatus().name(), data.status());
    assertEquals(0, data.products().size());
  }

  @Test
  void testGetCategoryDetails_whenIdDoesNotExist_ShouldError() {
    ResponseEntity<?> response =
        restTemplate.getForEntity("/api/v1/categories/999/details", Object.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    ResVM<?> responseBody = this.objectMapper.convertValue(response.getBody(), ResVM.class);
    assertNotNull(responseBody);
    assertEquals(HttpStatus.NOT_FOUND, responseBody.code());
    assertEquals("Category with id 999 not found.", responseBody.msg());
  }
}
