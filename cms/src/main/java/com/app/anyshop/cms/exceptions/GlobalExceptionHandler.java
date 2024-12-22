/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.exceptions;

import com.app.anyshop.cms.constant.Message;
import com.app.anyshop.cms.dto.ResVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResVM> notFoundException(NotFoundException e) {
    log.error("Not found exception: {}", e.getMessage());
    ResVM error = new ResVM(HttpStatus.NOT_FOUND, e.getMessage(), null);
    return ResponseEntity.ok(error);
  }

  @ExceptionHandler({ConstrainValidateFailedException.class})
  public ResponseEntity<ResVM> constrainValidateFailedException(
      ConstrainValidateFailedException ex) {
    log.error("ConstrainValidateFailedException: Validation error: {}", ex.getMessage());
    var errorRes = new ResVM(HttpStatus.BAD_REQUEST, Message.VALIDATE_FAILED, ex.getMessage());
    return ResponseEntity.ok(errorRes);
  }
}
