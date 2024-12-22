/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* *****************************************************************************************
 */

package db

import (
	"context"
	"time"
)

var (
	DefaultCacheExpireTime = 1
)

func GetDataFromKey(key string) (interface{}, error) {
	ctx := context.Background()

	redisClient := DB.Redis

	value, err := redisClient.Get(ctx, key).Result()

	return value, err
}

func SaveDataToCache(key string, data interface{}) error {
	ctx := context.Background()

	redisClient := DB.Redis
	err := redisClient.Set(ctx, key, data, time.Duration(DefaultCacheExpireTime)*time.Hour).Err()
	return err
}
