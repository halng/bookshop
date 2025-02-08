/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package utils

import (
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

func TestGenerateJWT(t *testing.T) {
	// Setup
	apiSecret := "testsecret"
	os.Setenv(EnvApiSecretKey, apiSecret)

	t.Run("Create and extract JWT", func(t *testing.T) {
		username := "changeme"
		id := "XXX-YYY-ZZZ"
		roles := []string{"super_admin"}
		permissions := []string{"read", "write"}

		// Test JWT
		token, err := GenerateJWT(id, username, roles, permissions)
		if err != nil {
			t.Errorf("Error generating JWT: %v", err)
		}

		if token == "" {
			t.Errorf("Token is empty")
		}
		
		assert.True(t, len(token) > 0)
	})
}
