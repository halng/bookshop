/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.controllers.cms;

import com.app.anyshop.api.controllers.Controller;
import io.swagger.v3.oas.annotations.Hidden;

public interface CMSController extends Controller {
  @Hidden String CMS_CATEGORY_PATH = BASE_V1 + "/cms/categories";
  @Hidden String CMS_PRODUCT_PATH = BASE_V1 + "/cms/products";
  @Hidden String CMS_PRODUCT_ATTRIBUTE_PATH = BASE_V1 + "/cms/product-attributes";
  @Hidden String CMS_PRODUCT_ATTRIBUTE_VALUE_PATH = BASE_V1 + "/cms/product-attribute-values";
}
