/**
 * *****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0;
 * *****************************************************************************************
 */
package com.app.anyshop.cms.controllers;

import com.app.anyshop.cms.annotation.ValidStatus;
import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import com.app.anyshop.cms.services.CMSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class CMSController {
  protected static final String BASE_V1 = "/api/v1";
  private final CMSService service;

  public CMSController(CMSService service) {
    this.service = service;
  }

  @Operation(summary = "Get all objects")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved objects"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping()
  ResponseEntity<PagingResVM> getAll(@RequestParam @Min(1) int page) {
    return this.service.getAll(page);
  }

  @Operation(summary = "Get object details by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved object details"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Object not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}")
  ResponseEntity<ResVM> getById(@NotBlank @PathVariable String id) {
    return this.service.getById(id);
  }

  @Operation(summary = "Create a new objects")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Object created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping()
  ResponseEntity<ResVM> create(@RequestBody Object obj) {
    return this.service.create(obj);
  }

  @Operation(summary = "Update a object by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Object updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Object not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PutMapping("/{id}")
  ResponseEntity<ResVM> update(@NotBlank @PathVariable String id, @RequestBody Object obj) {
    return this.service.update(id, obj);
  }

  @Operation(summary = "Update the status of a object by ID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Object status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Object not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PatchMapping("/{id}")
  ResponseEntity<ResVM> updateStatus(
      @NotBlank @PathVariable String id, @ValidStatus @RequestParam String status) {
    return this.service.updateStatus(id, status);
  }
}
