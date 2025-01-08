/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package handlers

import (
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/dto"
	"github.com/halng/anyshop/kafka"
	"github.com/halng/anyshop/logging"
	"github.com/halng/anyshop/models"
	"github.com/halng/anyshop/utils"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
)

// ========================= Functions =========================

// @BasePath /api/v1/user

// CreateStaff create a new staff account and send a message to kafka
// @Summary Create a new staff account
// @Schemes http
// @Description create a new staff account
// @Accept json
// @Produce json
// @Success 201 {string} string "Created"
// @Router /create-staff [post]
// func CreateStaff(c *gin.Context) {
// 	// check if requester is super admin
// 	requesterRole, _ := c.Get(constants.ApiUserRole)
// 	requesterId := c.GetHeader(constants.ApiUserIdRequestHeader)
// 	if !(requesterRole == models.RoleShopOwner || requesterRole == models.RoleShopManager) {
// 		ResponseErrorHandler(c, http.StatusForbidden, constants.InvalidPermission, nil)
// 		return
// 	}

// 	var userInput dto.RegisterRequest

// 	if err := c.ShouldBindJSON(&userInput); err != nil {
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, userInput)
// 		return
// 	}

// 	if ok, errors := utils.ValidateInput(userInput); !ok {
// 		ResponseErrorHandler(c, http.StatusBadRequest, errors, userInput)
// 		return
// 	}

// 	if models.ExistsByEmailOrUsername(userInput.Email, userInput.Username) {
// 		ResponseErrorHandler(c, http.StatusBadRequest, fmt.Sprintf(constants.AccountExists, userInput.Email, userInput.Username), userInput)
// 		return
// 	}

// 	// only for newly created user. default password is "123456"
// 	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(constants.DefaultPassword), bcrypt.DefaultCost)
// 	if err != nil {
// 		logging.LOGGER.Error("CreateStaff", zap.Error(err))
// 		ResponseErrorHandler(c, http.StatusInternalServerError, constants.InternalServerError, nil)
// 		return
// 	}
// 	// get role id for staff
// 	roleId, err := models.GetRoleIdByName(userInput.Role)
// 	if err != nil {
// 		logging.LOGGER.Error("Error when getting role id.", zap.Any("error", err))
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.BadRequest, nil)
// 		return
// 	}

// 	account := models.Account{}
// 	account.Email = userInput.Email
// 	account.Username = userInput.Username
// 	account.Password = string(hashedPassword)
// 	account.LastName = userInput.LastName
// 	account.FirstName = userInput.FirstName
// 	account.CreateBy = requesterId
// 	account.Status = constants.ACCOUNT_STATUS_INACTIVE
// 	account.RoleId = roleId
// 	_, err = account.SaveAccount()

// 	if err != nil {
// 		logging.LOGGER.Error("Error when saving account.", zap.Any("error", err))
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, account)
// 		return
// 	}

// 	// send msg to kafka
// 	serializedMessage := account.GenerateAndSaveSerializedMessageForActiveNewUser()

// 	kafka.PushMessageNewUser(serializedMessage)

// 	ResponseSuccessHandler(c, http.StatusCreated, nil)

// }

// func Register(c *gin.Context) {
// 	var userInput dto.RegisterRequest
// 	if err := c.ShouldBindJSON(&userInput); err != nil {
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, userInput)
// 		return
// 	}

// 	if ok, errors := utils.ValidateInput(userInput); !ok {
// 		ResponseErrorHandler(c, http.StatusBadRequest, errors, userInput)
// 		return
// 	}

// 	if models.ExistsByEmailOrUsername(userInput.Email, userInput.Username) {
// 		ResponseErrorHandler(c, http.StatusBadRequest, fmt.Sprintf(constants.AccountExists, userInput.Email, userInput.Username), userInput)
// 		return
// 	}

// 	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(userInput.Password), bcrypt.DefaultCost)
// 	if err != nil {
// 		logging.LOGGER.Error("Register", zap.Error(err))
// 		ResponseErrorHandler(c, http.StatusInternalServerError, constants.InternalServerError, nil)
// 		return
// 	}

// 	roleId, err := models.GetRoleIdByName(models.RoleUserBackOffice)
// 	if err != nil {
// 		logging.LOGGER.Error("Error when getting role id.", zap.Any("error", err))
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.BadRequest, nil)
// 	}

// 	account := models.Account{}
// 	account.Email = userInput.Email
// 	account.Username = userInput.Username
// 	account.Password = string(hashedPassword)
// 	account.LastName = userInput.LastName
// 	account.FirstName = userInput.FirstName
// 	account.CreateBy = "REGISTER"
// 	account.Status = constants.ACCOUNT_STATUS_INACTIVE
// 	account.RoleId = roleId
// 	_, err = account.SaveAccount()

// 	if err != nil {
// 		logging.LOGGER.Error("Error when saving account.", zap.Any("error", err))
// 		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, account)
// 		return
// 	}

// 	// send msg to kafka
// 	serializedMessage := account.GenerateAndSaveSerializedMessageForActiveNewUser()

// 	kafka.PushMessageNewUser(serializedMessage)

// 	ResponseSuccessHandler(c, http.StatusCreated, nil)
// }

func CreateStaff(c *gin.Context) {
	// Check requester permissions
	if !isAuthorized(c, []string{models.RoleShopOwner, models.RoleShopManager}) {
		ResponseErrorHandler(c, http.StatusForbidden, constants.InvalidPermission, nil)
		return
	}

	// Parse and validate input
	userInput, ok := parseAndValidateInput(c)
	if !ok {
		return
	}

	// Check if the account already exists
	if models.ExistsByEmailOrUsername(userInput.Email, userInput.Username) {
		ResponseErrorHandler(c, http.StatusBadRequest, fmt.Sprintf(constants.AccountExists, userInput.Email, userInput.Username), userInput)
		return
	}

	// Generate default password
	hashedPassword, err := generatePassword(constants.DefaultPassword)
	if err != nil {
		logErrorAndRespond(c, "CreateStaff", err, constants.InternalServerError)
		return
	}

	// Get role ID
	roleId, err := models.GetRoleIdByName(userInput.Role)
	if err != nil {
		logErrorAndRespond(c, "Error when getting role id.", err, constants.BadRequest)
		return
	}

	// Save account
	account := createAccount(userInput, hashedPassword, c.GetHeader(constants.ApiUserIdRequestHeader), roleId)
	if !saveAccountAndRespond(c, account) {
		return
	}

	// Send Kafka message
	sendKafkaMessage(account)

	ResponseSuccessHandler(c, http.StatusCreated, nil)
}

func Register(c *gin.Context) {
	// Parse and validate input
	userInput, ok := parseAndValidateInput(c)
	if !ok {
		return
	}

	// Check if the account already exists
	if models.ExistsByEmailOrUsername(userInput.Email, userInput.Username) {
		ResponseErrorHandler(c, http.StatusBadRequest, fmt.Sprintf(constants.AccountExists, userInput.Email, userInput.Username), userInput)
		return
	}

	// Hash password
	hashedPassword, err := generatePassword(userInput.Password)
	if err != nil {
		logErrorAndRespond(c, "Register", err, constants.InternalServerError)
		return
	}

	// Get role ID
	roleId, err := models.GetRoleIdByName(models.RoleUserBackOffice)
	if err != nil {
		logErrorAndRespond(c, "Error when getting role id.", err, constants.BadRequest)
		return
	}

	// Save account
	account := createAccount(userInput, hashedPassword, "REGISTER", roleId)
	if !saveAccountAndRespond(c, account) {
		return
	}

	// Send Kafka message
	sendKafkaMessage(account)

	ResponseSuccessHandler(c, http.StatusCreated, nil)
}

func isAuthorized(c *gin.Context, allowedRoles []string) bool {
	requesterRole, _ := c.Get(constants.ApiUserRole)
	for _, role := range allowedRoles {
		if requesterRole == role {
			return true
		}
	}
	return false
}

func parseAndValidateInput(c *gin.Context) (dto.RegisterRequest, bool) {
	var userInput dto.RegisterRequest
	if err := c.ShouldBindJSON(&userInput); err != nil {
		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, userInput)
		return userInput, false
	}

	if ok, errors := utils.ValidateInput(userInput); !ok {
		ResponseErrorHandler(c, http.StatusBadRequest, errors, userInput)
		return userInput, false
	}

	return userInput, true
}

func generatePassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

func createAccount(userInput dto.RegisterRequest, hashedPassword, createdBy string, roleId uuid.UUID) models.Account {
	return models.Account{
		Email:     userInput.Email,
		Username:  userInput.Username,
		Password:  hashedPassword,
		LastName:  userInput.LastName,
		FirstName: userInput.FirstName,
		CreateBy:  createdBy,
		Status:    constants.ACCOUNT_STATUS_INACTIVE,
		RoleId:    roleId,
	}
}

func saveAccountAndRespond(c *gin.Context, account models.Account) bool {
	_, err := account.SaveAccount()
	if err != nil {
		logging.LOGGER.Error("Error when saving account.", zap.Any("error", err))
		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, account)
		return false
	}
	return true
}

func sendKafkaMessage(account models.Account) {
	serializedMessage := account.GenerateAndSaveSerializedMessageForActiveNewUser()
	kafka.PushMessageNewUser(serializedMessage)
}

func logErrorAndRespond(c *gin.Context, logMessage string, err error, errorMessage string) {
	logging.LOGGER.Error(logMessage, zap.Error(err))
	ResponseErrorHandler(c, http.StatusInternalServerError, errorMessage, nil)
}

// Login verify user credentials and return uuid pair with token saved in redis
func Login(c *gin.Context) {
	var userInput dto.LoginRequest

	if err := c.ShouldBindJSON(&userInput); err != nil {
		ResponseErrorHandler(c, http.StatusBadRequest, constants.MessageErrorBindJson, userInput)
		return
	}

	var account models.Account
	var err error

	if account, err = models.GetAccountByUsername(userInput.Username); err != nil {
		ResponseErrorHandler(c, http.StatusNotFound, constants.AccountNotFound, userInput)
		return
	}

	if !account.ComparePassword(userInput.Password) {
		ResponseErrorHandler(c, http.StatusUnauthorized, constants.PasswordNotMatch, userInput)
		return
	}

	token := account.GenerateAccessToken()
	ResponseSuccessHandler(c, http.StatusOK, dto.LoginResponse{ApiToken: token, Username: account.Username, Email: account.Email, ID: account.ID.String()})

}

// Validate user credentials and return username and role
func Validate(c *gin.Context) {
	// get api token from header
	apiToken := c.GetHeader(constants.ApiTokenRequestHeader)
	userId := c.GetHeader(constants.ApiUserIdRequestHeader)

	if apiToken == "" || userId == "" {
		ResponseErrorHandler(c, http.StatusUnauthorized, constants.Unauthorized, apiToken)
		return
	}

	// get bearer token from redis
	hashMD5 := utils.ComputeMD5([]string{userId})
	accessToken, err := db.GetDataFromKey(fmt.Sprintf("%s_%s", hashMD5, apiToken))
	if err != nil || accessToken == nil || accessToken == "" {
		ResponseErrorHandler(c, http.StatusBadRequest, constants.TokenNotFount, accessToken)
		return
	}

	isValidToken, userId, username, role := utils.ExtractDataFromToken(accessToken.(string))
	if !isValidToken {
		ResponseErrorHandler(c, http.StatusInternalServerError, constants.InternalServerError, apiToken)
		return
	}
	c.JSON(200, gin.H{
		"username": username,
		"role":     role,
		"userId":   userId,
	})
}

func Activate(c *gin.Context) {
	token := c.Query("token")
	username := c.Query("username")

	if token == "" || username == "" {
		ResponseErrorHandler(c, http.StatusBadRequest, constants.MissingParams, nil)
		return
	}

	savedData, err := db.GetDataFromKey(fmt.Sprintf(constants.REDIS_PENDING_ACTIVE_STAFF_KEY, username))
	if err != nil || savedData == nil {
		ResponseErrorHandler(c, http.StatusNotFound, constants.TokenNotFount, nil)
		return
	}

	if token != savedData {
		ResponseErrorHandler(c, http.StatusUnauthorized, constants.Unauthorized, nil)
		return
	}

	account, err := models.GetAccountByUsername(username)
	if err != nil {
		ResponseErrorHandler(c, http.StatusNotFound, constants.AccountNotFound, nil)
		return
	}

	account.Status = constants.ACCOUNT_STATUS_ACTIVE
	err = account.UpdateAccount()
	if err != nil {
		ResponseErrorHandler(c, http.StatusInternalServerError, constants.InternalServerError, nil)
		return
	}

	ResponseSuccessHandler(c, http.StatusOK, nil)
}
