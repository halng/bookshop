package com.app.anyshop.cms.dto;

import org.springframework.http.HttpStatus;

public record ErrorVM(HttpStatus code, String msg) {}
