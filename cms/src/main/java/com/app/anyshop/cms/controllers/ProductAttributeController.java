/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.controllers;

import com.app.anyshop.cms.services.ProductAttributeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ProductAttributeController.PATH)
public class ProductAttributeController extends CMSController {
  protected static final String PATH = BASE_V1 + "/product-attributes";

  public ProductAttributeController(ProductAttributeService service) {
    super(service);
  }
}
