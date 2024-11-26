package main

import (
	"fmt"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/halng/bookshop/constants"
	"github.com/halng/bookshop/db"
	"github.com/halng/bookshop/handlers"
	"github.com/halng/bookshop/kafka"
	"github.com/halng/bookshop/logging"
	"github.com/halng/bookshop/middleware"
	"github.com/halng/bookshop/models"
	"github.com/joho/godotenv"
	"os"
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

	groupV1 := router.Group("/api/v1/iam")

	// routes
	groupV1.POST("/login", handlers.Login)
	groupV1.POST("/create-staff", middleware.ValidateRequest, handlers.CreateStaff)
	groupV1.GET("/validate", handlers.Validate)

	err = router.Run(":" + port)
	logging.LOGGER.Info(fmt.Sprintf("Starting web service on port %s", port))
	if err != nil {
		return
	}
}
