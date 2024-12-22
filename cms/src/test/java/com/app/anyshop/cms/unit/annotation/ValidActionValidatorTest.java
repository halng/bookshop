/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.unit.annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.app.anyshop.cms.annotation.validator.ValidActionValidator;
import org.junit.jupiter.api.Test;

public class ValidActionValidatorTest {
    private ValidActionValidator validator = new ValidActionValidator();

    @Test
    void testValidStatus() {
        assertTrue(validator.isValid("PAUSED", null));
    }

    @Test
    void testInvalidStatus() {
        assertFalse(validator.isValid("invalid status", null));
    }

    @Test
    void testNullStatus() {
        assertFalse(validator.isValid(null, null));
    }
}
