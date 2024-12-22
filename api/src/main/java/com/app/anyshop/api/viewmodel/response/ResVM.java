/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.viewmodel.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ResVM(HttpStatus code, Object data, String msg) {}
