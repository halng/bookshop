/*
 * ****************************************************************************************
 * Copyright 2024 By ANYSHOP Project
 * Licensed under the Apache License, Version 2.0;
 * ALL RIGHTS RESERVED
 * ****************************************************************************************
 */

package services

import (
	"github.com/gin-gonic/gin"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/dto"
	"github.com/halng/anyshop/kafka"
	"github.com/halng/anyshop/logging"
	"github.com/halng/anyshop/models"
	"github.com/halng/anyshop/utils"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
	"net/http"
)

// Register func to register new user account in the system
func Register(c *gin.Context) {
	userRegister, err := utils.ParseAndValidateInput(c, dto.RegisterRequest{})
	if err != nil {
		dto.BadRequestResponse(c, constants.MessageErrorBindJson, err)
		return
	}

	req := userRegister.(dto.RegisterRequest)

	if models.ExistsByEmailOrUsername(req.Email, req.Username) {
		dto.BadRequestResponse(c, constants.AccountExists, nil)
		return
	}

	hashedPassword, err := generatePassword(req.Password)
	if err != nil {
		dto.InternalServerErrorResponse(c, constants.InternalServerError, err)
		return
	}

	account := createAccount(req, hashedPassword, "REGISTER")
	if !saveAccountAndRespond(c, account) {
		dto.InternalServerErrorResponse(c, constants.InternalServerError, nil)
		return
	}

	role := models.Role{
		UserId:      account.ID,
		Roles:       []string{models.RoleSeller},
		Permissions: models.Permissions[models.RoleSeller],
	}

	if err = role.SaveRole(); err != nil {
		dto.InternalServerErrorResponse(c, constants.InternalServerError, err)
		return
	}

	sendKafkaMessage(account)
	dto.SuccessResponse(c, http.StatusCreated, constants.AccountCreated)
}

func Login(c *gin.Context) {
	userInput, err := utils.ParseAndValidateInput(c, dto.LoginRequest{})
	if err != nil {
		dto.BadRequestResponse(c, constants.MessageErrorBindJson, err)
		return

	}

	var account models.Account
	req := userInput.(dto.LoginRequest)

	if account, err = models.GetAccountByUsername(req.Username); err != nil {
		dto.NotFoundResponse(c, constants.AccountNotFound, nil)
		return
	}

	if !account.ComparePassword(req.Password) {
		dto.ForbiddenResponse(c, constants.PasswordNotMatch, userInput)
		return
	}

	roles, permissions, err := models.GetRoleAndPermissionsById(account.ID)
	if err != nil {
		logging.LOGGER.Error("Cannot get role for user %s ", zap.Any("account", account))
		dto.InternalServerErrorResponse(c, constants.InternalServerError, err)
	}
	jwtToken, err := utils.GenerateJWT(account.ID.String(), account.Username, roles, permissions)
	if err != nil {
		dto.InternalServerErrorResponse(c, constants.InternalServerError, err)
		return
	}

	dto.SuccessResponse(c, http.StatusOK, dto.LoginResponse{ApiToken: jwtToken, Username: account.Username, Email: account.Email, ID: account.ID.String()})

}

func Validate() {

}

func Activate() {

}

// PRIVATE FUNCTIONS

func createAccount(userInput dto.RegisterRequest, hashedPassword, createdBy string) models.Account {
	return models.Account{
		Email:       userInput.Email,
		Username:    userInput.Username,
		Password:    hashedPassword,
		LastName:    userInput.LastName,
		FirstName:   userInput.FirstName,
		CreateBy:    createdBy,
		Status:      constants.ACCOUNT_STATUS_INACTIVE,
		Permissions: make([]string, 0),
	}
}

func generatePassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

func saveAccountAndRespond(c *gin.Context, account models.Account) bool {
	_, err := account.SaveAccount()
	if err != nil {
		dto.InternalServerErrorResponse(c, constants.InternalServerError, err)
		return false
	}
	return true
}

func sendKafkaMessage(account models.Account) {
	serializedMessage := account.GenerateAndSaveSerializedMessageForActiveNewUser()
	kafka.PushMessageNewUser(serializedMessage)
}
