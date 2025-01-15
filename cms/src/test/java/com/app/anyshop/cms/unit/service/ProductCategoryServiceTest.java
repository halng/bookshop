/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.anyshop.cms.constant.Message;
import com.app.anyshop.cms.dto.*;
import com.app.anyshop.cms.entity.ProductCategory;
import com.app.anyshop.cms.entity.Status;
import com.app.anyshop.cms.exceptions.NotFoundException;
import com.app.anyshop.cms.repositories.IProductCategoryRepo;
import com.app.anyshop.cms.services.impl.ProductCategoryServiceImpl;
import com.app.anyshop.cms.utils.RedisUtils;
import java.util.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

  @Mock private IProductCategoryRepo productCategoryRepo;

  @Mock private RedisUtils redisUtils;

  @InjectMocks private ProductCategoryServiceImpl productCategoryService;

  private ProductCategory productCategory;

  @BeforeEach
  void setUp() {
    productCategory =
        ProductCategory.builder()
            .id("1")
            .name("Category1")
            .description("Description1")
            .status(Status.WAITING_APPROVAL)
            .build();

    SecurityContextHolder.getContext()
        .setAuthentication(
            new Authentication() {
              @Override
              public String getName() {
                return "";
              }

              @Override
              public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
              }

              @Override
              public Object getCredentials() {
                return null;
              }

              @Override
              public Object getDetails() {
                return null;
              }

              @Override
              public Object getPrincipal() {
                return new ImmutablePair<>("username", "userId");
              }

              @Override
              public boolean isAuthenticated() {
                return false;
              }

              @Override
              public void setAuthenticated(boolean isAuthenticated)
                  throws IllegalArgumentException {}
            });
  }

  @Test
  void testCreate() {
    CreateCategoryVM vm = new CreateCategoryVM("Category1", "Description1");
    when(productCategoryRepo.save(any(ProductCategory.class))).thenReturn(productCategory);

    ResVM response = productCategoryService.create(vm).getBody();

    assertEquals(HttpStatus.CREATED, response.code());
    assertEquals(Message.CREATED_WAITING_APPROVAL, response.msg());
    verify(productCategoryRepo, times(1)).save(any(ProductCategory.class));
  }

  @Test
  void testGetDetails_Success() {
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    ResVM response = productCategoryService.getById("1").getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.SUCCESS, response.msg());
    assertNotNull(response.data());
  }

  @Test
  void testGetDetails_NotFound() {
    when(productCategoryRepo.findById("1")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> productCategoryService.getById("1"));
  }

  @Test
  void testUpdateCategory_CannotUpdate() {
    productCategory.setStatus(Status.REMOVED);
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    CreateCategoryVM vm = new CreateCategoryVM("UpdatedName", "UpdatedDescription");
    ResVM response = productCategoryService.update("1", vm).getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.code());
    assertEquals(
        Message.CATEGORY_CAN_NOT_UPDATE.formatted(
            productCategory.getName(), productCategory.getStatus()),
        response.msg());
  }

  @Test
  void testUpdateCategory_DirectUpdate() {
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    CreateCategoryVM vm = new CreateCategoryVM("UpdatedName", "UpdatedDescription");
    ResVM response = productCategoryService.update("1", vm).getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.SUCCESS, response.msg());
    verify(productCategoryRepo, times(1)).save(any(ProductCategory.class));
  }

  @Test
  void testUpdateCategory_SaveToRedis() {
    productCategory.setStatus(Status.APPROVED);
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    CreateCategoryVM vm = new CreateCategoryVM("UpdatedName", "UpdatedDescription");
    ResVM response = productCategoryService.update("1", vm).getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.UPDATED_WAITING_APPROVAL, response.msg());
    verify(redisUtils, times(1))
        .saveDataToCache(anyString(), eq(vm), eq(Message.Constants.DEFAULT_EXPIRED_TIME));
  }

  @Test
  void testUpdateStatus_NoChange() {
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    ResVM response = productCategoryService.updateStatus("1", "WAITING_APPROVAL").getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.SUCCESS, response.msg());
    verify(productCategoryRepo, times(0)).save(any(ProductCategory.class));
  }

  @Test
  void testUpdateStatus_ChangeStatus() {
    when(productCategoryRepo.findById("1")).thenReturn(Optional.of(productCategory));

    ResVM response = productCategoryService.updateStatus("1", "APPROVED").getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.SUCCESS, response.msg());
    verify(productCategoryRepo, times(1)).save(any(ProductCategory.class));
  }

  @Test
  void testGetAll() {
    Page<ProductCategory> page = new PageImpl<>(Collections.singletonList(productCategory));
    when(productCategoryRepo.findAllByCreatedBy(anyString(), any(PageRequest.class)))
        .thenReturn(page);

    PagingResVM response = productCategoryService.getAll(1).getBody();

    assertEquals(HttpStatus.OK, response.code());
    assertEquals(Message.SUCCESS, response.msg());
    assertNotNull(response.data());
    assertEquals(1, response.paging().totalPages());
  }
}
