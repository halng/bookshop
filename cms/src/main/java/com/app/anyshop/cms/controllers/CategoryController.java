package com.app.anyshop.cms.controllers;

import com.app.anyshop.cms.annotation.ValidAction;
import com.app.anyshop.cms.dto.CreateCategoryVM;
import com.app.anyshop.cms.services.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final ProductCategoryService productCategoryService;

  @Autowired
  public CategoryController(ProductCategoryService productCategoryService) {
    this.productCategoryService = productCategoryService;
  }

  @PostMapping("")
  public ResponseEntity<?> createCategory(@RequestBody @Valid CreateCategoryVM vm) {
    var res = productCategoryService.create(vm);
    return ResponseEntity.ok(res);
  }

  @GetMapping("")
  public ResponseEntity<?> getAllCategories(@Min(1) @RequestParam int page) {
    var res = productCategoryService.getAll(page);
    return ResponseEntity.ok(res);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateCategory(
      @PathVariable String id, @Valid @RequestBody CreateCategoryVM vm) {
    var res = productCategoryService.update(id, vm);
    return ResponseEntity.ok(res);
  }

  @PutMapping("/status/{id}")
  public ResponseEntity<?> updateStatusCategory(
      @PathVariable String id, @ValidAction @RequestParam String action) {
    return ResponseEntity.ok(productCategoryService.update(id, action));
  }

  @GetMapping("/{id}/details")
  public ResponseEntity<?> getCategory(@PathVariable String id) {
    var res = productCategoryService.getDetails(id);
    return ResponseEntity.ok(res);
  }
}
