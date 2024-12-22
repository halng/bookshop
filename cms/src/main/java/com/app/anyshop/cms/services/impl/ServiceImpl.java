/*
* *****************************************************************************************
* Copyright 2024 By Hal Nguyen 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* *****************************************************************************************
*/

package com.app.anyshop.cms.services.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.security.core.context.SecurityContextHolder;

public class ServiceImpl {
  public static String getCurrentUser() {
    var auth =
        (ImmutablePair<String, String>)
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    // all requests have been authenticated
    return auth.getRight();
  }
}
