package com.app.anyshop.cms.dto;

import org.springframework.http.HttpStatus;

public record ResVM<T, K>(HttpStatus code, String msg, T data, K errors) {}
