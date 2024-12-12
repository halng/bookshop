package com.app.anyshop.cms.dto;

import org.springframework.http.HttpStatus;

public record PagingResVM<T, V>(
    HttpStatus code, String msg, T data, PagingObjectVM paging, V error) {}
