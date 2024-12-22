/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* *****************************************************************************************
 */

package middleware

import (
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/handlers"
	"github.com/halng/anyshop/utils"
)

func ValidateRequest(c *gin.Context) {
	// get api token from header
	apiToken := c.GetHeader(constants.ApiTokenRequestHeader)
	userId := c.GetHeader(constants.ApiUserIdRequestHeader)

	if apiToken == "" || userId == "" {
		handlers.ResponseErrorHandler(c, http.StatusUnauthorized, constants.MissingCredentials, nil)
		return
	}

	// get bearer token from redis
	hashMD5 := utils.ComputeMD5([]string{userId})
	accessToken, err := db.GetDataFromKey(fmt.Sprintf("%s_%s", hashMD5, apiToken))
	if err != nil || accessToken == nil || accessToken == "" {
		handlers.ResponseErrorHandler(c, http.StatusUnauthorized, constants.TokenNotFount, accessToken)
		return
	}

	isValidToken, userId, username, role := utils.ExtractDataFromToken(accessToken.(string))
	if !isValidToken {
		handlers.ResponseErrorHandler(c, http.StatusUnauthorized, constants.TokenNotFount, apiToken)
		return
	}

	c.Header(constants.ApiUserIdRequestHeader, userId)
	c.Header(constants.ApiUserRole, role)
	c.Header(constants.ApiUserRequestHeader, username)

	c.Set(constants.ApiUserRole, role)
	c.Next()
}
