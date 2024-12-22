/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.app.anyshop.cms.dto.ResVM;
import com.app.anyshop.cms.exceptions.GlobalExceptionHandler;
import com.app.anyshop.cms.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

  @InjectMocks private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void notFoundExceptionReturnsNotFoundStatus() {
    NotFoundException exception = new NotFoundException("Resource not found");

    ResponseEntity<ResVM> response = globalExceptionHandler.notFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getBody().code());
    assertEquals("Resource not found", response.getBody().msg());
  }

  @Test
  void notFoundExceptionHandlesNullMessage() {
    NotFoundException exception = new NotFoundException(null);

    ResponseEntity<ResVM> response = globalExceptionHandler.notFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getBody().code());
    assertEquals(null, response.getBody().msg());
  }
}
