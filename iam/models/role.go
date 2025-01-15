/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package models

import (
	"github.com/google/uuid"
	"github.com/halng/anyshop/db"
	"github.com/lib/pq"
)

const (
	// within the user
	RoleUserBackOffice = "user:back_office" // newly created user. With this role user can only create shop and update their profile
	RoleUserShopFront  = "user:shop_front"  // newly created user for store front only. With this role user can only shopping and perform related actions
	// Within the app
	RoleAppOwner  = "app:owner" // a owner of the app and can do anything
	RoleAppReader = "app:read"  // a reader of the app and can read anything
	RoleAppWriter = "app:write" // a writer of the app and can write anything
	// Within the shop
	RoleShopOwner   = "shop:owner"   // Has all permissions
	RoleShopReader  = "shop:read"    // Can read shop data
	RoleShopWriter  = "shop:write"   // Can update or create new shop data
	RoleShopManager = "shop:manager" // have permission the same as owner but cannot delete the shop
)

var RolePermissions = map[string][]string{
	RoleAppOwner:       {"read", "update", "delete", "create", "approve"},
	RoleAppReader:      {"read"},
	RoleAppWriter:      {"read", "update", "create"},
	RoleShopOwner:      {"read", "update", "delete", "create", "approve"},
	RoleShopReader:     {"read"},
	RoleShopWriter:     {"read", "update", "create"},
	RoleShopManager:    {"read", "update", "create", "approve"},
	RoleUserBackOffice: {"create", "update"},
	RoleUserShopFront:  {"create", "update", "read", "update"},
}

type Role struct {
	ID          uuid.UUID      `gorm:"primaryKey" json:"id"`
	Name        string         `gorm:"unique;not null" json:"name"`
	Permissions pq.StringArray `gorm:"type:text[]" json:"permissions"`
	Account     []Account      `gorm:"foreignKey:RoleID"`
}

func GetRoleIdByName(name string) (uuid.UUID, error) {
	var roleID uuid.UUID
	err := db.DB.Postgres.Model(&Role{}).Where("name = ?", name).Select("id").Row().Scan(&roleID)
	if err != nil {
		return uuid.UUID{}, err
	}
	return roleID, nil
}

func GetRoleById(id uuid.UUID) (string, error) {
	var role Role
	err := db.DB.Postgres.Where("id = ?", id).First(&role).Error
	if err != nil {
		return "", err
	}
	return role.Name, nil
}

func GetPermissionsByName(name string) ([]string, error) {
	var role Role
	err := db.DB.Postgres.Where("name = ?", name).First(&role).Error
	if err != nil {
		return nil, err
	}
	return role.Permissions, nil
}
