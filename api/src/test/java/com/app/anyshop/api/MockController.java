/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/
package com.app.anyshop.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
