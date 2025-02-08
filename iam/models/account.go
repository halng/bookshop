/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package models

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/google/uuid"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/dto"
	"github.com/halng/anyshop/logging"
	"github.com/halng/anyshop/utils"
	"golang.org/x/crypto/bcrypt"
)

type Account struct {
	ID          uuid.UUID `gorm:"primaryKey" json:"id"`
	Username    string    `gorm:"unique;not null" json:"username"`
	Password    string    `json:"password"`
	Email       string    `gorm:"unique;not null" json:"email"`
	FirstName   string    `json:"firstName"`
	LastName    string    `json:"lastName"`
	Permissions []string  `json:"permissions"`
	Status      string    `json:"status"`
	CreateAt    int64     `json:"createAt"`
	UpdateAt    int64     `json:"updateAt"`
	CreateBy    string    `json:"createBy"`
	UpdateBy    string    `json:"updateBy"`
}

func (account *Account) SaveAccount() (*Account, error) {

	account.ID = uuid.New()
	account.CreateAt = time.Now().Unix()
	if account.CreateBy != "" {
		account.CreateBy = constants.DefaultCreator
	}

	if err := db.DB.Postgres.Create(&account).Error; err != nil {
		return &Account{}, err
	}

	return account, nil

}

func (account *Account) UpdateAccount() error {
	if err := db.DB.Postgres.Save(&account).Error; err != nil {
		return err
	}
	return nil
}

func (account *Account) BeforeSave() error {
	account.UpdateAt = time.Now().Unix()
	account.UpdateBy = account.Username
	return nil
}

func ExistsByEmailOrUsername(email string, username string) bool {
	var count int64
	db.DB.Postgres.Model(&Account{}).Where("email = ? OR username = ?", email, username).Count(&count)
	return count > 0
}

func GetAccountByUsername(username string) (Account, error) {
	var account Account
	err := db.DB.Postgres.Model(&Account{}).Where(" username = ?", username).Take(&account).Error
	return account, err
}

func (account *Account) ComparePassword(password string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(account.Password), []byte(password))
	return err == nil
}

func (account *Account) GenerateAndSaveSerializedMessageForActiveNewUser() string {
	var activeNewUser dto.ActiveNewUser
	activeNewUser.Username = account.Username
	activeNewUser.Email = account.Email
	activeNewUser.Token = utils.ComputeHMAC256(account.Username, account.Email)
	expiredTime := time.Now().UnixMilli() + 1000*60*60*24
	activeNewUser.ExpiredTime = fmt.Sprintf("%d", expiredTime) // 1 day

	// build activation link for new user
	apiHost := os.Getenv("API_GATEWAY_HOST")
	activeNewUser.ActivationLink = fmt.Sprintf("%s/api/v1/iam/activate?username=%s&token=%s", apiHost, activeNewUser.Username, activeNewUser.Token)

	// save active token to redis
	key := fmt.Sprintf(constants.REDIS_PENDING_ACTIVE_STAFF_KEY, activeNewUser.Username)
	err := db.SaveDataToCache(key, activeNewUser.Token)
	if err != nil {
		logging.LOGGER.Error("Cannot save token in cache")
		return ""
	}

	var activeNewUserMsg dto.ActiveNewUserMsg
	activeNewUserMsg.Action = constants.ActiveNewUserAction
	activeNewUserMsg.Data = activeNewUser

	serialized, err := json.Marshal(activeNewUserMsg)
	if err != nil {
		log.Printf("Cannot serialize data")
		return ""
	}
	return string(serialized)
}
