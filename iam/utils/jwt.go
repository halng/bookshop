/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package utils

import (
	"github.com/golang-jwt/jwt/v5"
	"os"
	"time"
)

var (
	EnvApiSecretKey = "API_SECRET"
)

func GenerateJWT(id string, username string, roles, permissions []string) (string, error) {
	apiSecret := os.Getenv(EnvApiSecretKey)

	claims := jwt.MapClaims{
		"sub":         id,
		"name":        username,
		"role":        roles,
		"permissions": permissions,
		"exp":         time.Now().Add(time.Hour * 24).Unix(),
		"iat":         time.Now().Unix(),
		"iss":         "iam",
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)

	return token.SignedString([]byte(apiSecret))
}
