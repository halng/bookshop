/*
 * ****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * ****************************************************************************************
 */

package com.app.anyshop.cms.unit.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.app.anyshop.cms.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(SwaggerConfig.class)
public class SwaggerConfigTest {

  @Qualifier("globalHeadersCustomizer")
  @Autowired
  private OpenApiCustomizer openApiCustomizer;

  @Test
  public void testGlobalHeadersCustomizer() {
    OpenAPI openAPI = new OpenAPI();
    Paths paths = new Paths();
    PathItem pathItem = new PathItem();
    paths.addPathItem("/test", pathItem);
    openAPI.setPaths(paths);

    openApiCustomizer.customise(openAPI);

    pathItem
        .readOperations()
        .forEach(
            operation -> {
              List<Parameter> parameters = operation.getParameters();
              assertThat(parameters)
                  .extracting(Parameter::getName)
                  .contains("X-API-USER-ID", "X-API-USER", "X-API-USER-ROLE");
            });
  }
}
