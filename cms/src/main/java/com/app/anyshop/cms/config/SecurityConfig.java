/*
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * *****************************************************************************************
 */

package com.app.anyshop.cms.config;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {
  private final RequestFilter requestFilter;

  @Autowired
  public SecurityConfig(RequestFilter requestFilter) {
    this.requestFilter = requestFilter;
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return webSecurity ->
        webSecurity
            .ignoring()
            .requestMatchers(
                "/actuator/prometheus",
                "/actuator/prometheus/**",
                "/swagger-ui",
                "/swagger-ui/**",
                "/error",
                "/v3/api-docs/**");
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowedHeaders("*");
      }
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(
                        "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/error")
                    .permitAll()
                    .requestMatchers(regexMatcher(".*\\?status=.*"))
                    .hasAuthority("super_admin")
                    .anyRequest()
                    .authenticated());

    http.csrf(AbstractHttpConfigurer::disable).cors(AbstractHttpConfigurer::disable);
    http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
