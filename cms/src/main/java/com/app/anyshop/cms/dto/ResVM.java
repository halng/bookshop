package com.app.anyshop.cms.dto;

import org.springframework.http.HttpStatus;

public record ResVM<T>(HttpStatus code, String msg, T data) {}
