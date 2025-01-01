/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
 */

package main

import (
	"fmt"
	"github.com/halng/anyshop/docs"
	"os"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/halng/anyshop/constants"
	"github.com/halng/anyshop/db"
	"github.com/halng/anyshop/handlers"
	"github.com/halng/anyshop/kafka"
	"github.com/halng/anyshop/logging"
	"github.com/halng/anyshop/middleware"
	"github.com/halng/anyshop/models"
	"github.com/joho/godotenv"
	swaggerfiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

func main() {
	logging.InitLogging()

	// connect database
	db.ConnectDB()
	models.Initialize()

	var err error

	_ = godotenv.Load(".env")

	// init kafka server
	bootstrapServer := os.Getenv("KAFKA_HOST")
	err = kafka.InitializeKafkaProducer(bootstrapServer)
	if err != nil {
		panic(err)
	}

	port := os.Getenv("PORT")

	router := gin.Default()

	// set up cors origin
	router.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Accept", "Authorization", constants.ApiTokenRequestHeader, constants.ApiUserIdRequestHeader},
		AllowCredentials: true,
	}))

	userGroup := router.Group("/api/v1/user")
	// user routes
	userGroup.POST("/login", handlers.Login)
	userGroup.POST("/create-staff", middleware.ValidateRequest, handlers.CreateStaff)
	userGroup.POST("/register", handlers.Register)
	userGroup.GET("/validate", handlers.Validate)
	userGroup.POST("/activate", handlers.Activate)

	// shop routes
	shopGroup := router.Group("/api/v1/shop")
	shopGroup.POST("", middleware.ValidateRequest, handlers.CreateShop)
	shopGroup.PUT("", middleware.ValidateRequest, handlers.UpdateShop)

	// swagger
	docs.SwaggerInfo.BasePath = "/api/v1"
	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerfiles.Handler))

	err = router.Run(":" + port)
	logging.LOGGER.Info(fmt.Sprintf("Starting web service on port %s", port))
	if err != nil {
		return
	}
}
