/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.api.viewmodel;

import java.util.Date;

import org.springframework.http.HttpStatusCode;

public record ResVm(HttpStatusCode statusCode, String err, String errDetail, Throwable cause, Date date) {
}