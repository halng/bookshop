/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package models

import (
	"os"

	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/logging"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
)

func Initialize() {
	DB := db.DB
	DB.Postgres.AutoMigrate(&Account{})
	DB.Postgres.AutoMigrate(&Role{})
	initMasterUser()

}

func initMasterUser() {
	masterUsername := os.Getenv("MASTER_USERNAME")
	masterPassword := os.Getenv("MASTER_PASSWORD")
	masterEmail := os.Getenv("MASTER_EMAIL")
	masterFirstName := os.Getenv("MASTER_FIRST_NAME")
	masterLastName := os.Getenv("MASTER_LAST_NAME")

	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(masterPassword), bcrypt.DefaultCost)
	if err != nil {
		logging.LOGGER.Error("Cannot hash password", zap.Any("error", err))
		panic("Cannot hash password")
	}
	masterAccount := Account{
		Username:  masterUsername,
		Password:  string(hashedPassword),
		Email:     masterEmail,
		FirstName: masterFirstName,
		LastName:  masterLastName}

	savedAcc, err := masterAccount.SaveAccount()
	if err != nil {
		logging.LOGGER.Error("Cannot create master account", zap.Any("error", err))
		panic("Cannot create master account")
	}

	role := Role{
		UserId:      savedAcc.ID,
		Roles:       []string{RoleAppOwner},
		Permissions: Permissions[RoleAppOwner],
	}

	if err = role.SaveRole(); err != nil {
		logging.LOGGER.Error("Cannot create master role", zap.Any("error", err))
		panic("Cannot create master role")
	}

	logging.LOGGER.Info("Master account created successfully: " + masterUsername + " - " + masterPassword)
}
