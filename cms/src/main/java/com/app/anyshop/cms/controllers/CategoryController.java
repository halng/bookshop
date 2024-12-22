/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.controllers;

import com.app.anyshop.cms.services.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CategoryController.PATH)
public class CategoryController extends CMSController {
  protected static final String PATH = BASE_V1 + "/categories";

  @Autowired
  public CategoryController(ProductCategoryService productCategoryService) {
    super(productCategoryService);
  }
}
