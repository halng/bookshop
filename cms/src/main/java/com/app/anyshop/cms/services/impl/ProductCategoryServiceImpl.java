package com.app.anyshop.cms.services.impl;

import com.app.anyshop.cms.constant.Message;
import com.app.anyshop.cms.dto.*;
import com.app.anyshop.cms.entity.ProductCategory;
import com.app.anyshop.cms.entity.Status;
import com.app.anyshop.cms.exceptions.NotFoundException;
import com.app.anyshop.cms.repositories.IProductCategoryRepo;
import com.app.anyshop.cms.services.ProductCategoryService;
import com.app.anyshop.cms.utils.RedisUtils;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("rawtypes")
public class ProductCategoryServiceImpl implements ProductCategoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

  private final IProductCategoryRepo productCategoryRepo;
  private final RedisUtils redisUtils;

  @Autowired
  public ProductCategoryServiceImpl(
      IProductCategoryRepo productCategoryRepo, RedisUtils redisUtils) {
    this.productCategoryRepo = productCategoryRepo;
    this.redisUtils = redisUtils;
  }

  protected ProductCategory getCategory(String id) {
    return productCategoryRepo
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Category with id %s not found.".formatted(id)));
  }

  @Override
  public ResVM create(CreateCategoryVM vm) {
    LOGGER.info("Create Category with name {}.", vm.name());

    var productCategory =
        ProductCategory.builder()
            .name(vm.name())
            .description(vm.description())
            .status(Status.WAITING_APPROVAL)
            .build();
    var created = productCategoryRepo.save(productCategory);

    LOGGER.info(
        "Request from {} for create Category with name {} - Successfully",
        created.getCreatedBy(),
        vm.name());

    return new ResVM<>(HttpStatus.CREATED, Message.CREATED_WAITING_APPROVAL, null, null);
  }

  @Override
  public ResVM getDetails(String id) {
    var productCategory = this.getCategory(id);

    var productList = productCategory.getProducts();
    Set<ProductResVM> productResVMS = new HashSet<>();
    for (var product : productList) {
      productResVMS.add(
          new ProductResVM(
              product.getId(), product.getName(), product.getPrice(), product.getStatus().name()));
    }

    var result =
        new DetailProductCategoryResVM(
            productCategory.getId(),
            productCategory.getName(),
            productCategory.getDescription(),
            productCategory.getStatus().name(),
            productResVMS);

    return new ResVM<>(HttpStatus.OK, Message.SUCCESS, result, null);
  }

  @Override
  public ResVM update(String id, CreateCategoryVM vm) {
    var currentUser = ServiceImpl.getCurrentUser();
    LOGGER.info("Receive Request Update category with id {} from user {}.", id, currentUser);

    var productCategory = this.getCategory(id);
    if (productCategory.getStatus() == Status.REMOVED
        || productCategory.getStatus() == Status.REJECTED) {
      LOGGER.info(
          "Category {} have status {}, can not update.",
          productCategory.getName(),
          productCategory.getStatus());
      return new ResVM<>(
          HttpStatus.BAD_REQUEST,
          Message.CATEGORY_CAN_NOT_UPDATE.formatted(
              productCategory.getName(), productCategory.getStatus()),
          null,
          null);
    }

    if (productCategory.getStatus() == Status.WAITING_APPROVAL) {
      LOGGER.info("Update category with id {} directly.", id);
      productCategory.setName(vm.name());
      productCategory.setDescription(vm.description());
      productCategoryRepo.save(productCategory);
      return new ResVM<>(HttpStatus.OK, Message.SUCCESS, null, null);
    }

    // in case it's in use
    var key = Message.REDIS_KEY_UPDATE_CATEGORY_TEMPLATE.formatted(currentUser, id);
    redisUtils.saveDataToCache(key, vm, Message.Constants.DEFAULT_EXPIRED_TIME);
    LOGGER.info("Update category with id {} and save to redis. Waiting for approval", id);

    return new ResVM<>(HttpStatus.OK, Message.UPDATED_WAITING_APPROVAL, null, null);
  }

  @Override
  public ResVM update(String id, String status) {
    var currentUser = ServiceImpl.getCurrentUser();
    LOGGER.info(
        "Receive Request Update Status {} of category with id {} from user {}.",
        status,
        id,
        currentUser);

    var productCategory = this.getCategory(id);
    if (productCategory.getStatus() == Status.valueOf(status)) {
      LOGGER.info("Nothing change, done.");
      return new ResVM<>(HttpStatus.OK, Message.SUCCESS, null, null);
    }

    productCategory.setStatus(Status.valueOf(status));
    productCategoryRepo.save(productCategory);

    return new ResVM<>(HttpStatus.OK, Message.SUCCESS, null, null);
  }

  @Override
  public PagingResVM getAll(int page) {
    var user = ServiceImpl.getCurrentUser();
    LOGGER.info("Receive Request Get All Categories with page {}.", page);

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
    return new PagingResVM<>(HttpStatus.OK, Message.SUCCESS, result, pagingObj, null);
  }
}
