package com.app.anyshop.cms.utils;

import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class EntityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Pair<String, String> principal = (Pair<String, String>) authentication.getPrincipal();
            // TODO: Make sure it works
            return Optional.of(principal.getRight());
        }
        return Optional.of("SYSTEM");
    }
}