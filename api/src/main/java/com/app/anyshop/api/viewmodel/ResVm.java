/*
* *****************************************************************************************
* Copyright 2024 By ANYSHOP Project 
* Licensed under the Apache License, Version 2.0;
* *****************************************************************************************
*/

package com.app.anyshop.api.viewmodel;

import java.util.Date;

import org.springframework.http.HttpStatusCode;

public record ResVm(HttpStatusCode statusCode, String err, String errDetail, Throwable cause, Date date) {
}