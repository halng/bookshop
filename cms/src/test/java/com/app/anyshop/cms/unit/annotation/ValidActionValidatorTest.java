/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.unit.annotation;

import static org.junit.jupiter.api.Assertions.*;

import com.app.anyshop.cms.annotation.validator.StatusValidator;
import com.app.anyshop.cms.exceptions.ConstrainValidateFailedException;
import org.junit.jupiter.api.Test;

public class ValidActionValidatorTest {
  private StatusValidator validator = new StatusValidator();

  @Test
  void testValidStatus() {
    assertTrue(validator.isValid("PAUSED", null));
  }

  @Test
  void testInvalidStatus() {
    assertThrows(
        ConstrainValidateFailedException.class, () -> validator.isValid("invalid status", null));
  }

  @Test
  void testNullStatus() {
    assertFalse(validator.isValid(null, null));
  }
}
