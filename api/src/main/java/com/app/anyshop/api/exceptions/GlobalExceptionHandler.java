/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.exceptions;

import com.app.anyshop.api.viewmodel.response.ResVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({ConstrainValidateFailedException.class, IllegalArgumentException.class})
  public ResponseEntity<ResVM> constrainValidateFailedException(
      ConstrainValidateFailedException ex) {
    log.error("ConstrainValidateFailedException: Validation error: {}", ex.getMessage());
    var errorRes =
        new ResVM(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "The request is invalid. Please review your input or contact support for assistance.");
    return ResponseEntity.ok(errorRes);
  }
}
