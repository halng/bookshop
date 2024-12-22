/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.helper;

import com.app.anyshop.api.exceptions.ConstrainValidateFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectValidator {
  private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private static final Validator validator = factory.getValidator();
  private static final ObjectMapper mapper = new ObjectMapper();

  private static <T> T convertToRecord(Object source, Class<T> clazz) {
    return mapper.convertValue(source, clazz);
  }

  public static <T> T validate(Object source, Class<T> clazz) {

    var record = convertToRecord(source, clazz);
    var violations = validator.validate(record);
    if (!violations.isEmpty()) {
      var message =
          violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toArray();
      throw new ConstrainValidateFailedException(Arrays.toString(message));
    }
    return record;
  }
}
