/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.repositories;

import com.app.anyshop.cms.entity.ProductCategory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductCategoryRepo extends JpaRepository<ProductCategory, String> {
  Page<ProductCategory> findAllByCreatedBy(String createdBy, Pageable pageable);

  // for testing
  Optional<ProductCategory> findByName(String name);
}
