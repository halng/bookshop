/*
 * *****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.annotation.validator;

import com.app.anyshop.cms.annotation.ValidStatus;
import com.app.anyshop.cms.entity.Status;
import com.app.anyshop.cms.exceptions.ConstrainValidateFailedException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidStatus} annotation.
 *
 * <p>This validator checks if the value of the annotated field is a valid {@link Status} value. The
 * value is considered valid if it can be parsed into a {@link Status} enum. If the value is {@code
 * null}, it is considered invalid.
 */
public class StatusValidator implements ConstraintValidator<ValidStatus, Object> {

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    if (null == o) {
      return false;
    }
    try {
      Status.valueOf(o.toString());
      return true;
    } catch (Exception e) {
      throw new ConstrainValidateFailedException("Invalid Status.");
    }
  }
}
