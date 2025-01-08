/*
 * ****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * ****************************************************************************************
 */

package com.app.anyshop.cms.services.impl;

import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import com.app.anyshop.cms.services.ProductAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductAttributeServiceImpl implements ProductAttributeService {
  @Override
  public ResponseEntity<PagingResVM> getAll(int page) {
    return ResponseEntity.ok(PagingResVM.builder().msg("GetALL ProductAttribute").build());
  }

  @Override
  public ResponseEntity<ResVM> getById(String id) {
    return ResponseEntity.ok(ResVM.builder().msg("GetById ProductAttribute").build());
  }

  @Override
  public ResponseEntity<ResVM> create(Object obj) {
    return ResponseEntity.ok(ResVM.builder().msg("Create ProductAttribute").build());
  }

  @Override
  public ResponseEntity<ResVM> update(String id, Object obj) {
    return ResponseEntity.ok(ResVM.builder().msg("Update ProductAttribute").build());
  }

  @Override
  public ResponseEntity<ResVM> updateStatus(String id, String status) {
    return ResponseEntity.ok(ResVM.builder().msg("UpdateStatus ProductAttribute").build());
  }
}
