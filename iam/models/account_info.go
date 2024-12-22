/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* *****************************************************************************************
 */

package models

// AccountInfo struct
type AccountInfo struct {
	ID               string `json:"id"`
	AccountId        string `json:"account_id"`
	FirstName        string `json:"firstname"`
	LastName         string `json:"lastname"`
	PhoneNumber      string `json:"phone_number"`
	PrimaryAddress   string `json:"primary_address"`
	SecondaryAddress string `json:"secondary_address"`
}
