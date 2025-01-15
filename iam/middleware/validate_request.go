/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package middleware

import (
	"fmt"
	"net/http"
	"slices"
	"strings"

	"github.com/halng/anyshop/models"

	"github.com/gin-gonic/gin"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/handlers"
	"github.com/halng/anyshop/utils"
)

var MethodPermission = map[string]string{
	"GET":    "read",
	"POST":   "create",
	"PUT":    "update",
	"DELETE": "delete",
	"PATCH":  "approve",
}

func ValidateRequest(c *gin.Context) {
	// get api token from header
	apiToken := c.GetHeader(constants.ApiTokenRequestHeader)
	userId := c.GetHeader(constants.ApiUserIdRequestHeader)
	originMethod := c.GetHeader(constants.ApiOriginMethod)
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

	canPerform := ValidateRole(c, role, originMethod)
	if !canPerform {
		handlers.ResponseErrorHandler(c, http.StatusForbidden, constants.ForbiddenMissingPermission, nil)
		return
	}

	c.Header(constants.ApiUserIdRequestHeader, userId)
	c.Header(constants.ApiUserRole, role)
	c.Header(constants.ApiUserRequestHeader, username)

	c.Set(constants.ApiUserRole, role)
	c.Next()
}

// ValidateRole Validate user based on role and permission
func ValidateRole(c *gin.Context, role string, originMethod string) bool {
	permissions, err := models.GetPermissionsByName(role)
	if err != nil {
		handlers.ResponseErrorHandler(c, http.StatusInternalServerError, constants.InternalServerError, nil)
	}

	neededPermission := MethodPermission[strings.ToUpper(originMethod)]

	return slices.Contains(permissions, neededPermission)
}
