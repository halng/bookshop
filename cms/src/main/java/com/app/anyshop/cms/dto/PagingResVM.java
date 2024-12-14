package com.app.anyshop.cms.dto;

import org.springframework.http.HttpStatus;

public record PagingResVM<T>(
    HttpStatus code, String msg, T data, PagingObjectVM paging) {}
