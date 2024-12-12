package com.app.anyshop.cms.repositories;

import com.app.anyshop.cms.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductCategoryRepo extends JpaRepository<ProductCategory, String> {
    Page<ProductCategory> findAllByCreatedBy(String createdBy, Pageable pageable);
}
