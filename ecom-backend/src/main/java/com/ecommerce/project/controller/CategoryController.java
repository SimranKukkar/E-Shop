package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CategoryController {

  @Autowired CategoryService categoryService;

  @GetMapping("/public/categories")
  public ResponseEntity<CategoryResponse> getAllCategories(
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) int pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) int pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY) String sortBy,
      @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) String sortOrder) {

    return new ResponseEntity<>(
        categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
  }

  @PostMapping("/public/categories")
  public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
    CategoryDTO categoryCreated = categoryService.createCategory(category);
    return new ResponseEntity<>(categoryCreated, HttpStatus.CREATED);
  }

  @DeleteMapping("/admin/categories/{categoryId}")
  public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
    CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
    return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
  }

  @PutMapping("/public/categories/{categoryId}")
  public ResponseEntity<CategoryDTO> updateCategory(
      @Valid @RequestBody CategoryDTO updatedCategory, @PathVariable Long categoryId) {

    CategoryDTO categoryDTO = categoryService.updateCategory(updatedCategory, categoryId);
    return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
  }
}
