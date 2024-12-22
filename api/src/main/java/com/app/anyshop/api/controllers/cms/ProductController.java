/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.controllers.cms;

import com.app.anyshop.api.viewmodel.response.ResVM;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CMSController.CMS_PRODUCT_PATH)
public class ProductController implements CMSController {
  @Override
  public ResponseEntity<ResVM> getAll(int page) {
    return null;
  }

  @Override
  public ResponseEntity<ResVM> getById(String id) {
    return null;
  }

  @Override
  public ResponseEntity<ResVM> create(Object obj) {
    return null;
  }

  @Override
  public ResponseEntity<ResVM> update(String id, Object obj) {
    return null;
  }

  @Override
  public ResponseEntity<ResVM> updateStatus(String id, String action) {
    return null;
  }
}
