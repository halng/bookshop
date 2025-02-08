/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package models

import (
	"github.com/google/uuid"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/lib/pq"
	"time"
)

const (
	// within the user
	RoleSeller = "seller" // newly created user. With this role user can only create shop and update their profile
	RoleBuyer  = "buyer"  // newly created user for store front only. With this role user can only shopping and perform related actions
	// Within the app
	RoleAppOwner   = "app:owner"   // a owner of the app and can do anything
	RoleAppManager = "app:manager" // a reader of the app and can read anything
	RoleAppStaff   = "app:staff"   // a writer of the app and can write anything
	// Within the shop
	RoleShopOwner   = "shop:owner"   // Has all permissions
	RoleShopManager = "shop:manager" // Can read shop data
	RoleShopStaff   = "shop:staff"   // Can update or create new shop data
)

var Permissions = map[string][]string{
	RoleSeller:      {"shop:create", "account:read", "account:update", "account:delete"},
	RoleBuyer:       {"shopping"},
	RoleAppOwner:    {"shop:delete", "account:delete", "shop:read", "account:read", "shop:delete"},
	RoleAppManager:  {""},
	RoleAppStaff:    {"", ""},
	RoleShopOwner:   {"shop:read", "shop:update", "shop:delete", "staff:create", ""},
	RoleShopManager: {""},
	RoleShopStaff:   {""},
}

type Role struct {
	UserId      uuid.UUID      `json:"userId"`
	Roles       pq.StringArray `gorm:"type:text[]" json:"roles"`
	Permissions pq.StringArray `gorm:"type:text[]" json:"permissions"`
	CreateAt    int64          `json:"createAt"`
	UpdateAt    int64          `json:"updateAt"`
	CreateBy    string         `json:"createBy"`
	UpdateBy    string         `json:"updateBy"`
}

func (role *Role) SaveRole() error {

	role.CreateAt = time.Now().Unix()
	if role.CreateBy != "" {
		role.CreateBy = constants.DefaultCreator
	}

	if err := db.DB.Postgres.Create(&role).Error; err != nil {
		return err
	}

	return nil

}

func (role *Role) BeforeSave() error {
	role.UpdateAt = time.Now().Unix()
	role.UpdateBy = constants.DefaultCreator
	return nil
}

func GetRoleIdByName(name string) (uuid.UUID, error) {
	var roleID uuid.UUID
	err := db.DB.Postgres.Model(&Role{}).Where("name = ?", name).Select("id").Row().Scan(&roleID)
	if err != nil {
		return uuid.UUID{}, err
	}
	return roleID, nil
}

func GetRoleAndPermissionsById(userId uuid.UUID) ([]string, []string, error) {
	var role Role
	err := db.DB.Postgres.Where("userId = ?", userId).First(&role).Error
	if err != nil {
		return nil, nil, err
	}
	return role.Roles, role.Permissions, nil
}

func GetPermissionsByName(name string) ([]string, error) {
	var role Role
	err := db.DB.Postgres.Where("name = ?", name).First(&role).Error
	if err != nil {
		return nil, err
	}
	return role.Permissions, nil
}
