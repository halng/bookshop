package com.app.anyshop.cms.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryVM(@NotBlank String name, @NotBlank String description) {}
