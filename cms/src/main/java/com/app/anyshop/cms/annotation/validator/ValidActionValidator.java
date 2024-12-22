/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.annotation.validator;

import com.app.anyshop.cms.annotation.ValidAction;
import com.app.anyshop.cms.entity.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidAction} annotation.
 * <p>
 *     This validator checks if the value of the annotated field is a valid {@link Status} value.
 *     The value is considered valid if it can be parsed into a {@link Status} enum.
 *     If the value is {@code null}, it is considered invalid.
 *  </p>
 */
public class ValidActionValidator implements ConstraintValidator<ValidAction, Object> {

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
    if (null == o) {
      return false;
    }
    try {
      Status.valueOf(o.toString());
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
