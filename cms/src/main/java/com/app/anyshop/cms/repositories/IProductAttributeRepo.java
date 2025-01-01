/*
 * ****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0;
 * ****************************************************************************************
 */

package com.app.anyshop.cms.repositories;

import com.app.anyshop.cms.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductAttributeRepo extends JpaRepository<ProductAttribute, String> {
}
