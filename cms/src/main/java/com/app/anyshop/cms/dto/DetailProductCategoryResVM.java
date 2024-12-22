/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.dto;

import java.util.Set;

public record DetailProductCategoryResVM(
    String id, String name, String description, String status, Set<ProductResVM> products) {}
