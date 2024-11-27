package handlers

import (
	"encoding/json"
	"net/http"
	"os"
	"testing"

	"github.com/halng/bookshop/constants"
	handlers2 "github.com/halng/bookshop/handlers"
	"github.com/halng/bookshop/middleware"
	"github.com/halng/bookshop/test/integration"
	"github.com/stretchr/testify/assert"
)

func TestLoginHandler(t *testing.T) {
	urlPathLogin := "/api/v1/login"
	router := integration.SetUpRouter()

	router.POST(urlPathLogin, handlers2.Login)

	t.Run("Login: when master credential is used", func(t *testing.T) {
		// Act
		validJsonRequest := `{"password": "changeme", "username": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathLogin, validJsonRequest)

		// Assert
		if code != http.StatusOK {
			t.Errorf("Expected status code %d but got %d", http.StatusOK, code)
		}
		assert.Equal(t, code, http.StatusOK)
		assert.Contains(t, res, "token")
	})
	t.Run("Login: when data invalid to bind json", func(t *testing.T) {
		// Act
		invalidJsonRequest := `{""password": "changeme" "username": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathLogin, invalidJsonRequest)

		assert.Equal(t, code, http.StatusBadRequest)
		assert.Equal(t, res, `{"code":400,"error":"Please check your input. Something went wrong","status":"ERROR"}`)
	})
	t.Run("Login: when user is not found", func(t *testing.T) {
		// Act
		validJsonRequest := `{"password": "not-found", "username": "not-found"}`
		code, res := integration.ServeRequest(router, "POST", urlPathLogin, validJsonRequest)

		assert.Equal(t, code, http.StatusNotFound)
		assert.Equal(t, res, `{"code":404,"error":"Account not found","status":"ERROR"}`)
	})
	t.Run("Login: when account exist and password is not match", func(t *testing.T) {
		// account register successfully in #TestRegister: Register when user is successfully registered
		// check
		jsonLoginRequest := `{"password": "not-match", "username": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathLogin, jsonLoginRequest)

		assert.Equal(t, code, http.StatusUnauthorized)
		assert.Equal(t, res, `{"code":401,"error":"Invalid credentials","status":"ERROR"}`)

	})
}


// TestCreateStaffHandler combine both test for middleware and CreateStaff Func
func TestCreateStaffHandler(t *testing.T) {
	urlPathRegister := "/api/v1/create-staff"

	headers := map[string]string{
		constants.ApiTokenRequestHeader:  "test-secret",
		constants.ApiUserIdRequestHeader: "test-user",
	}

	router := integration.SetUpRouter()
	router.POST(urlPathRegister, middleware.ValidateRequest, handlers2.CreateStaff)

	t.Run("Create Staff: when request not fulfill - Missing Header", func(t *testing.T) {
		// Act
		code, res := integration.ServeRequest(router, "POST", urlPathRegister, "")

		// Assert
		if code != http.StatusUnauthorized {
			t.Errorf("Expected status code %d but got %d", http.StatusBadRequest, code)
		}
		expectedResponse := `{"code":401,"error":"Missing credentials. X-API-SECRET-TOKEN and X-API-USER-ID are required","status":"ERROR"}`
		assert.Equal(t, expectedResponse, res)
		assert.Equal(t, 401, code)
	})

	// t.Run("Create Staff: when requester have permission", func(t *testing.T) {
	// 	code, res := integration.ServeRequest(router, "POST", urlPathLogin, jsonLoginData)

	// 	assert.Equal(t, 200, code)
	// 	assert.Contains(t, res, "token")

	// 	var encodedRes dto.LoginResponse
	// 	err := json.Unmarshal([]byte(res), &encodedRes)
	// 	assert.NoError(t, err)

	// 	headers[constants.ApiTokenRequestHeader] = encodedRes.ApiToken
	// 	headers[constants.ApiUserIdRequestHeader] = encodedRes.ID

	// 	code, res, _ = integration.ServeRequestWithHeader(
	// 		router,
	// 		"POST",
	// 		urlPathRegister,
	// 		`{"email":"test@gmail.com",
	// 				"username": "test",
	// 				"password": "test",
	// 				"lastname": "test",
	// 				"firstname": "test"}`,
	// 		headers)

	// 	assert.Equal(t, 201, code)

	// })

	t.Run("Create Staff: when email is invalid and missing field", func(t *testing.T) {
		// Act
		invalidUserInput := `{"email":"this-is-not-valid-email","username": "changeme", "lastname": "changeme"}`

		code, res, _ := integration.ServeRequestWithHeader(router, "POST", urlPathRegister, invalidUserInput, headers)

		if code != http.StatusBadRequest {
			t.Errorf("Expected status code %d but got %d", http.StatusBadRequest, code)
		}

		expectedResponse := `{"code":400,"error":{"0":"The email field must be a valid email address","1":"The firstname field is required"},"status":"ERROR"}`
		assert.Equal(t, expectedResponse, res)
		assert.Equal(t, 400, code)
	})
	t.Run("Create Staff: when data is unable to bind to json", func(t *testing.T) {

		// Act
		invalidUserInput := `{"email":"this-is-not-valid-email","userName": "changeme", "lastname": "changeme"}`
		code, res, _ := integration.ServeRequestWithHeader(router, "POST", urlPathRegister, invalidUserInput, headers)

		// Assert
		if code != http.StatusBadRequest {
			t.Errorf("Expected status code %d but got %d", http.StatusBadRequest, code)
		}

		expectedResponse := `{"code":400,"error":"Please check your input. Something went wrong","status":"ERROR"}`
		assert.Equal(t, expectedResponse, res)
		assert.Equal(t, 400, code)
	})
	t.Run("Create Staff: when user is successfully registered", func(t *testing.T) {
		// Act
		validUserInput := `{"email":"changeme@gmail.com", "username": "changeme", "password": "changeme", "lastname": "changeme", "firstname": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathRegister, validUserInput)

		// Assert
		if code != http.StatusCreated {
			t.Errorf("Expected status code %d but got %d", http.StatusCreated, code)
		}
		expectedResponse := `{"code":201,"data":null,"status":"SUCCESS"}`
		assert.Equal(t, expectedResponse, res)
	})
	t.Run("Create Staff: when user is exists", func(t *testing.T) {
		// Act
		validUserInput := `{"email":"changeme@gmail.com", "username": "changeme", "password": "changeme", "lastname": "changeme", "firstname": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathRegister, validUserInput)

		// Assert
		if code != http.StatusBadRequest {
			t.Errorf("Expected status code %d but got %d", http.StatusBadRequest, code)
		}
		expectedResponse := `{"code":400,"error":"Account with username: changeme@gmail.com or email: changeme already exists","status":"ERROR"}`
		assert.Equal(t, expectedResponse, res)
	})
}

func TestValidate(t *testing.T) {
	urlPathValidate := "/api/v1/validate"
	urlPathLogin := "/api/v1/login"

	router := integration.SetUpRouter()

	router.GET(urlPathValidate, handlers2.Validate)
	router.POST(urlPathLogin, handlers2.Login)

	t.Run("Validate: when api token is missing", func(t *testing.T) {
		// Act
		code, res := integration.ServeRequest(router, "GET", urlPathValidate, "")

		// Assert
		assert.Equal(t, code, http.StatusUnauthorized)
		assert.Equal(t, res, `{"code":401,"error":"Unauthorized","status":"ERROR"}`)
	})
	t.Run("Validate: when api token is invalid", func(t *testing.T) {
		// Act
		code, res, _ := integration.ServeRequestWithHeader(router, "GET", urlPathValidate, "", nil)

		// Assert
		assert.Equal(t, code, http.StatusUnauthorized)
		assert.Equal(t, res, `{"code":401,"error":"Unauthorized","status":"ERROR"}`)

	})
	t.Run("Validate: when user token was expired", func(t *testing.T) {
		headers := map[string]string{
			constants.ApiTokenRequestHeader: "XXX",
			constants.ApiUserIdRequestHeader: "USER_ID",
		}

		// act
		code, res, _ := integration.ServeRequestWithHeader(router, "GET", urlPathValidate, "", headers)
		
		// Assert
		assert.Equal(t, code, http.StatusBadRequest)
		assert.Equal(t,res, `{"code":400,"error":"Your login session has expired. Please login again","status":"ERROR"}`)
	})

	t.Run("Validate: when system cannot extract data from token", func(t *testing.T) {
		// Login 
		loginJsonRequest := `{"password": "changeme", "username": "changeme"}`
		code, res := integration.ServeRequest(router, "POST", urlPathLogin, loginJsonRequest)

		var data map[string]interface{}
		err := json.Unmarshal([]byte(res), &data)
		
		var resData map[string]interface{}
		err = json.Unmarshal([]byte(data["data"].(string)), &resData)
		assert.NoError(t, err)

		headers := map[string]string{
			constants.ApiTokenRequestHeader: resData["api-token"].(string),
			constants.ApiUserIdRequestHeader: resData["id"].(string),
		}

		// act
		code, res, _ = integration.ServeRequestWithHeader(router, "GET", urlPathValidate, "", headers)
		
		// Assert
		assert.Equal(t, code, http.StatusInternalServerError)
		assert.Equal(t,res, `{"code":500,"error":"There was an error processing your request. Please try again later","status":"ERROR"}`)
	})
}

func TestMain(m *testing.M) {
	integration.SetupTestServer()

	code := m.Run()

	integration.TearDownContainers()
	os.Exit(code)
}
