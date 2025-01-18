/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0;
 * *****************************************************************************************
 */

package com.app.anyshop.cms.dto;

import java.util.Set;

public record DetailProductCategoryResVM(
    String id, String name, String description, String status, Set<ProductResVM> products) {}
