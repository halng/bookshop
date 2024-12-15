package com.app.anyshop.cms.services;

import com.app.anyshop.cms.dto.CreateCategoryVM;
import com.app.anyshop.cms.dto.PagingResVM;
import com.app.anyshop.cms.dto.ResVM;
import org.springframework.stereotype.Service;

/** Service interface for managing product categories. */
@Service
@SuppressWarnings("rawtypes")
public interface ProductCategoryService {

  /**
   * Creates a new product category.
   *
   * @param vm the view model containing the details of the category to be created
   * @return a response view model containing the status and message of the operation
   */
  ResVM create(CreateCategoryVM vm);

  /**
   * Retrieves the details of a product category by its ID.
   *
   * @param id the ID of the product category
   * @return a response view model containing the status, message, and details of the product
   *     category
   */
  ResVM getDetails(String id);

  /**
   * Updates an existing product category. First make sure that the current user have the right to
   * update the category. Second, make sure that the category is not in the status of `REMOVED` or
   * `REJECTED`. Third, if the current category status is `WAITING_APPROVAL`, then this category can
   * update directly in database. Fourth, if the current category status is `APPROVED`, then this
   * category can't update directly in database, instead, it will be save update version in redis
   * for a week, If it get approved by admin, then it will be updated in database.
   *
   * @param vm the view model containing the updated details of the category
   * @return a response view model containing the status and message of the operation
   */
  ResVM update(String id, CreateCategoryVM vm);

  /**
   * Updates the status of a product category by its ID.
   *
   * @param id the ID of the product category
   * @param status the new status of the product category
   * @return a response view model containing the status and message of the operation
   */
  ResVM update(String id, String status);

  /**
   * Retrieves a paginated list of all product categories.
   *
   * @param page the page number to retrieve
   * @return a response view model containing the status, message, and list of product categories
   */
  PagingResVM getAll(int page);
}
