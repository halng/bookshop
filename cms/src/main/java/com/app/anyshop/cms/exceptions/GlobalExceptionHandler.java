package com.app.anyshop.cms.exceptions;

import com.app.anyshop.cms.dto.ErrorVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorVM> notFoundException(NotFoundException e) {
    log.error("Not found exception: {}", e.getMessage());
    ErrorVM error = new ErrorVM(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.ok(error);
  }
}
