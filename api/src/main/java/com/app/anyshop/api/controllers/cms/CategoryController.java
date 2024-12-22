/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.controllers.cms;

import com.app.anyshop.api.helper.ObjectValidator;
import com.app.anyshop.api.viewmodel.request.cms.CreateCategoryVM;
import com.app.anyshop.api.viewmodel.response.ResVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CMSController.CMS_CATEGORY_PATH)
public class CategoryController implements CMSController {

  @Override
  public ResponseEntity<ResVM> getAll(int page) {
    return ResponseEntity.ok(new ResVM(HttpStatus.OK, null, "getAll"));
  }

  @Override
  public ResponseEntity<ResVM> getById(String id) {
    return ResponseEntity.ok(ResVM.builder().code(HttpStatus.OK).msg("getById").build());
  }

  @Override
  public ResponseEntity<ResVM> create(Object obj) {
    var createCategoryVM = ObjectValidator.validate(obj, CreateCategoryVM.class);
    return ResponseEntity.ok(ResVM.builder().code(HttpStatus.CREATED).msg("create").build());
  }

  @Override
  public ResponseEntity<ResVM> update(String id, Object obj) {
    var createCategoryVM = ObjectValidator.validate(obj, CreateCategoryVM.class);
    return ResponseEntity.ok(ResVM.builder().code(HttpStatus.ACCEPTED).msg("update").build());
  }

  @Override
  public ResponseEntity<ResVM> updateStatus(String id, String action) {
    return ResponseEntity.ok(ResVM.builder().code(HttpStatus.ACCEPTED).msg("updateStatus").build());
  }
}
