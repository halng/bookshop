/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record PagingResVM(HttpStatus code, String msg, Object data, PagingObjectVM paging) {}
