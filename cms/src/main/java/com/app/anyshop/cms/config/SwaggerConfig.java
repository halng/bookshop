package com.app.anyshop.cms.config;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenApiCustomizer globalHeadersCustomizer() {
    return openApi ->
        openApi
            .getPaths()
            .values()
            .forEach(
                pathItem ->
                    pathItem
                        .readOperations()
                        .forEach(
                            operation -> {
                              // Add X-API-USER-ID header globally
                              operation.addParametersItem(
                                  new Parameter()
                                      .in("header")
                                      .name("X-API-USER-ID")
                                      .description("Custom user ID header")
                                      .required(false));

                              // Add X-API-USER header globally
                              operation.addParametersItem(
                                  new Parameter()
                                      .in("header")
                                      .name("X-API-USER")
                                      .description("Custom user header")
                                      .required(false));

                              // Add X-API-USER-ROLE header globally
                              operation.addParametersItem(
                                  new Parameter()
                                      .in("header")
                                      .name("X-API-USER-ROLE")
                                      .description("Custom user role header")
                                      .required(false));
                            }));
  }
}
