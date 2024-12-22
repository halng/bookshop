/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* *****************************************************************************************
 */

package logging

import (
	"encoding/json"

	"go.uber.org/zap"
)

var LOGGER *zap.Logger

func InitLogging() {
	rawJsonConfig := []byte(`
	{
		"level": "debug",
		"encoding": "json",
		"outputPaths": ["stdout", "/tmp/logs"],
		  "encoderConfig": {
			"messageKey": "message",
			"levelKey": "level",
			"levelEncoder": "lowercase"
		  }
	}`,
	)

	var config zap.Config
	if err := json.Unmarshal(rawJsonConfig, &config); err != nil {
		panic(err)
	}

	LOGGER = zap.Must(config.Build())

	LOGGER.Info("Starting up")
}
