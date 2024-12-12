package com.app.anyshop.cms.services.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.context.SecurityContextHolder;

public class ServiceImpl {
  public static String getCurrentUser() {
    var auth =
        (Pair<String, String>)
            SecurityContextHolder.getContext().getAuthentication().getCredentials();
    // all requests have been authenticated
    return auth.getRight();
  }
}
