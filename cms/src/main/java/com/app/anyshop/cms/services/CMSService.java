/*
 * ****************************************************************************************
 * Copyright 2024 By Hal Nguyen
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * ****************************************************************************************
 */

package com.app.anyshop.cms.services;

import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import org.springframework.http.ResponseEntity;

public interface CMSService {
  /**
   * Retrieves all objects with pagination.
   *
   * @param page the page number to retrieve, must be greater than or equal to 1
   * @return a ResponseEntity containing a PagingResVM with the paginated objects
   */
  ResponseEntity<PagingResVM> getAll(int page);

  /**
   * Retrieves the details of an object by its ID.
   *
   * @param id the ID of the object to retrieve, must not be blank
   * @return a ResponseEntity containing a ResVM with the object details
   */
  ResponseEntity<ResVM> getById(String id);

  /**
   * Creates a new object.
   *
   * @param obj the object to create
   * @return a ResponseEntity containing a ResVM with the created object
   */
  ResponseEntity<ResVM> create(Object obj);

  /**
   * Updates an existing object by its ID.
   *
   * @param id the ID of the object to update, must not be blank
   * @param obj the updated object
   * @return a ResponseEntity containing a ResVM with the updated object
   */
  ResponseEntity<ResVM> update(String id, Object obj);

  /**
   * Updates the status of an object by its ID.
   *
   * @param id the ID of the object to update, must not be blank
   * @param status the new status of the object
   * @return a ResponseEntity containing a ResVM with the updated status
   */
  ResponseEntity<ResVM> updateStatus(String id, String status);
}
