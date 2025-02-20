/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0;
 * *****************************************************************************************
 */

package com.app.anyshop.cms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryVM(@NotBlank String name, @NotBlank String description) {}
