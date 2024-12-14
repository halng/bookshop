package com.app.anyshop.cms.utils;

import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class EntityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            ImmutablePair<String, String> principal = (ImmutablePair<String, String>) authentication.getPrincipal();
            return Optional.of(principal.getRight());
        }
        return Optional.of("SYSTEM");
    }
}