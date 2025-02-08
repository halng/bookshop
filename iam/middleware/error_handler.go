/*
 * ****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0;
 * ALL RIGHTS RESERVED
 * ****************************************************************************************
 */

package middleware

import (
	"errors"
	"github.com/gin-gonic/gin"
	"github.com/halng/anyshop/dto"
	"net/http"
)

// ErrorHandler is a middleware for handling errors
func ErrorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next() // Process request

		// Handle 404 (Not Found) error
		if c.Writer.Status() == http.StatusNotFound {
			dto.ErrorResponse(c, http.StatusNotFound, "Resource not found", nil)
			return
		}

		// If any errors exist in the Gin context
		if len(c.Errors) > 0 {
			err := c.Errors.Last().Err

			var apiErr *dto.APIResponse
			// Check if the error is of type *APIError.
			if errors.As(err, &apiErr) {
				c.JSON(apiErr.Code, apiErr)
			} else {
				// For unknown errors, avoid leaking internal details.
				c.JSON(http.StatusInternalServerError, &dto.APIResponse{
					Code:   http.StatusInternalServerError,
					Status: "ERROR",
					Data:   nil,
					Error:  "Internal server error",
				})
			}
			// Abort to ensure no further handlers are executed.
			c.Abort()
		}
	}
}
