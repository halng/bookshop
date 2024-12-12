package com.app.anyshop.cms.dto;

import java.util.Set;

public record DetailProductCategoryResVM(
    String id, String name, String description, String status, Set<ProductResVM> products) {}
