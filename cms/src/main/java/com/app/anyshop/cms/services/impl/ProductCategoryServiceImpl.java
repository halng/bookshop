/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.services.impl;

import com.app.anyshop.cms.constant.Message;
import com.app.anyshop.cms.dto.*;
import com.app.anyshop.cms.entity.ProductCategory;
import com.app.anyshop.cms.entity.Status;
import com.app.anyshop.cms.exceptions.NotFoundException;
import com.app.anyshop.cms.repositories.IProductCategoryRepo;
import com.app.anyshop.cms.services.ProductCategoryService;
import com.app.anyshop.cms.utils.ObjectValidator;
import com.app.anyshop.cms.utils.RedisUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
  private final IProductCategoryRepo productCategoryRepo;
  private final RedisUtils redisUtils;

  protected ProductCategory getCategory(String id) {
    return productCategoryRepo
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Category with id %s not found.".formatted(id)));
  }

  public ProductCategoryServiceImpl(
      IProductCategoryRepo productCategoryRepo, RedisUtils redisUtils) {
    this.productCategoryRepo = productCategoryRepo;
    this.redisUtils = redisUtils;
  }

  @Override
  public ResponseEntity<PagingResVM> getAll(int page) {
    var user = ServiceImpl.getCurrentUser();
    log.info("Receive Request Get All Categories with page {}.", page);

    Pageable pageable = PageRequest.of(page - 1, Message.Constants.MAX_PER_REQUEST);
    Page<ProductCategory> productCategories =
        productCategoryRepo.findAllByCreatedBy(user, pageable);

    Set<DetailProductCategoryResVM> result = new HashSet<>();
    for (var productCategory : productCategories.getContent()) {
      result.add(
          new DetailProductCategoryResVM(
              productCategory.getId(),
              productCategory.getName(),
              productCategory.getDescription(),
              productCategory.getStatus().name(),
              null));
    }

    var pagingObj =
        new PagingObjectVM(productCategories.getTotalPages(), productCategories.getTotalElements());
    return ResponseEntity.ok(new PagingResVM(HttpStatus.OK, Message.SUCCESS, result, pagingObj));
  }

  @Override
  public ResponseEntity<ResVM> getById(String id) {
    var productCategory = this.getCategory(id);

    var productList = productCategory.getProducts();
    Set<ProductResVM> productResVMS = new HashSet<>();
    if (productList != null) {
      for (var product : productList) {
        productResVMS.add(
            new ProductResVM(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStatus().name()));
      }
    }

    var result =
        new DetailProductCategoryResVM(
            productCategory.getId(),
            productCategory.getName(),
            productCategory.getDescription(),
            productCategory.getStatus().name(),
            productResVMS);

    return ResponseEntity.ok(new ResVM(HttpStatus.OK, Message.SUCCESS, result));
  }

  @Override
  public ResponseEntity<ResVM> create(Object obj) {
    var vm = ObjectValidator.validate(obj, CreateCategoryVM.class);
    log.info("Create Category with name {}.", vm.name());

    var productCategory =
        ProductCategory.builder()
            .name(vm.name())
            .description(vm.description())
            .status(Status.WAITING_APPROVAL)
            .build();
    var created = productCategoryRepo.save(productCategory);

    log.info(
        "Request from {} for create Category with name {} - Successfully",
        created.getCreatedBy(),
        vm.name());

    return ResponseEntity.ok(new ResVM(HttpStatus.CREATED, Message.CREATED_WAITING_APPROVAL, null));
  }

  @Override
  public ResponseEntity<ResVM> update(String id, Object obj) {
    var vm = ObjectValidator.validate(obj, CreateCategoryVM.class);
    var currentUser = ServiceImpl.getCurrentUser();
    log.info("Receive Request Update category with id {} from user {}.", id, currentUser);

    var productCategory = this.getCategory(id);
    if (productCategory.getStatus() == Status.REMOVED
        || productCategory.getStatus() == Status.REJECTED) {
      log.info(
          "Category {} have status {}, can not update.",
          productCategory.getName(),
          productCategory.getStatus());
      return ResponseEntity.ok(
          new ResVM(
              HttpStatus.BAD_REQUEST,
              Message.CATEGORY_CAN_NOT_UPDATE.formatted(
                  productCategory.getName(), productCategory.getStatus()),
              null));
    }

    if (productCategory.getStatus() == Status.WAITING_APPROVAL) {
      log.info("Update category with id {} directly.", id);
      productCategory.setName(vm.name());
      productCategory.setDescription(vm.description());
      productCategoryRepo.save(productCategory);
      return ResponseEntity.ok(new ResVM(HttpStatus.OK, Message.SUCCESS, null));
    }

    // in case it's in use
    var key = Message.REDIS_KEY_UPDATE_CATEGORY_TEMPLATE.formatted(currentUser, id);
    redisUtils.saveDataToCache(key, vm, Message.Constants.DEFAULT_EXPIRED_TIME);
    log.info("Update category with id {} and save to redis. Waiting for approval", id);

    return ResponseEntity.ok(new ResVM(HttpStatus.OK, Message.UPDATED_WAITING_APPROVAL, null));
  }

  @Override
  public ResponseEntity<ResVM> updateStatus(String id, String status) {
    var currentUser = ServiceImpl.getCurrentUser();
    log.info(
        "Receive Request Update Status {} of category with id {} from user {}.",
        status,
        id,
        currentUser);

    var productCategory = this.getCategory(id);
    if (productCategory.getStatus() == Status.valueOf(status)) {
      log.info("Nothing change, done.");
      return ResponseEntity.ok(new ResVM(HttpStatus.OK, Message.SUCCESS, null));
    }

    productCategory.setStatus(Status.valueOf(status));
    productCategoryRepo.save(productCategory);

    return ResponseEntity.ok(new ResVM(HttpStatus.OK, Message.SUCCESS, null));
  }

  //
  //  @Override
  //  public ResVM update(String id, String status) {
  //    var currentUser = ServiceImpl.getCurrentUser();
  //    LOGGER.info(
  //        "Receive Request Update Status {} of category with id {} from user {}.",
  //        status,
  //        id,
  //        currentUser);
  //
  //    var productCategory = this.getCategory(id);
  //    if (productCategory.getStatus() == Status.valueOf(status)) {
  //      LOGGER.info("Nothing change, done.");
  //      return new ResVM(HttpStatus.OK, Message.SUCCESS, null);
  //    }
  //
  //    productCategory.setStatus(Status.valueOf(status));
  //    productCategoryRepo.save(productCategory);
  //
  //    return new ResVM(HttpStatus.OK, Message.SUCCESS, null);
  //  }
  //
  //  @Override
  //  public PagingResVM getAll(int page) {

  //  }
}
