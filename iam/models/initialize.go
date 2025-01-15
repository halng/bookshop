/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package models

import (
	"os"

	"github.com/google/uuid"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/logging"
	"github.com/jinzhu/gorm"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
)

func Initialize() {
	DB := db.DB
	DB.Postgres.AutoMigrate(&Account{})
	DB.Postgres.AutoMigrate(&Role{})
	initRole(DB.Postgres)
	initMasterUser()

}

// InitRole auto create roles when the app starts
func initRole(db *gorm.DB) {
	roles := []string{RoleAppOwner, RoleAppReader, RoleAppWriter, RoleShopOwner, RoleShopReader, RoleShopWriter, RoleShopManager, RoleUserBackOffice, RoleUserShopFront}

	for _, roleName := range roles {
		role := Role{ID: uuid.New(), Name: roleName, Permissions: RolePermissions[roleName]}
		if err := db.Create(&role).Error; err != nil {
			logging.LOGGER.Error("Error occurred when creating role",
				zap.String("roleName", roleName),
				zap.Any("err", err))

		} else {
			logging.LOGGER.Info("Role created successfully", zap.String("roleName", roleName))
		}
	}

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

	// get role and assign for master account
	roleId, err := GetRoleIdByName(RoleShopOwner)
	if err != nil {
		logging.LOGGER.Error("Cannot get role id", zap.Any("error", err))
		panic("Cannot get role id for super admin")
	}

	masterAccount.RoleId = roleId

	_, err = masterAccount.SaveAccount()
	if err != nil {
		logging.LOGGER.Error("Cannot create master account", zap.Any("error", err))
		panic("Cannot create master account")
	}

	logging.LOGGER.Info("Master account created successfully: " + masterUsername + " - " + masterPassword)
}
