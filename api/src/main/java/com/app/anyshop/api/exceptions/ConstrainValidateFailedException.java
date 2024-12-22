/*
 * ****************************************************************************************
 * Copyright (c) 2024, Hal Ng (halng) - haonguyentan2001@gmail.com
 * Licensed under the MIT License. See LICENSE file in the project root for details.
 * ****************************************************************************************
 */

package com.app.anyshop.api.exceptions;

public class ConstrainValidateFailedException extends RuntimeException {

  public ConstrainValidateFailedException(String message) {
    super(message);
  }

  public ConstrainValidateFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConstrainValidateFailedException(Throwable cause) {
    super(cause);
  }
}
