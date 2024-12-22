/*
 * ****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * ****************************************************************************************
 */

package com.app.anyshop.cms.services.impl;

import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import com.app.anyshop.cms.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  @Override
  public ResponseEntity<PagingResVM> getAll(int page) {
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
  public ResponseEntity<ResVM> updateStatus(String id, String status) {
    return null;
  }
}
