package com.app.anyshop.cms.unit.annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.app.anyshop.cms.annotation.validator.ValidActionValidator;

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
