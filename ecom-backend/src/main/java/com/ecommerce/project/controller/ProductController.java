package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {
  @Autowired private ProductService productService;

  @GetMapping("/public/products")
  public ResponseEntity<ProductResponse> getAllProducts( @RequestParam(name = "keyword", required = false) String keyword,
                                                         @RequestParam(name = "category", required = false) String category,
      @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE,required = false) int pageSize,
      @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false) String sortBy,
      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
    ProductResponse productResponse =
        productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder,keyword,category);
    return ResponseEntity.ok(productResponse);
  }

  @PostMapping("/admin/categories/{categoryId}/product")
  public ResponseEntity<ProductDTO> addProduct(
          @Valid @RequestBody ProductDTO product, @PathVariable Long categoryId) {
    ProductDTO productDTO = productService.addProduct(categoryId, product);
    return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.CREATED);
  }

  @GetMapping("/public/categories/{categoryId}/products")
  public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
    ProductResponse productResponse = productService.getProductsByCategory(categoryId);
    return ResponseEntity.ok(productResponse);
  }

  @GetMapping("/public/products/keyword/{keyword}")
  public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {

    ProductResponse productResponse = productService.getProductsByKeyword(keyword);
    return new ResponseEntity<ProductResponse>(productResponse, HttpStatus.FOUND);
  }

  @PutMapping("/admin/products/{productId}")
  public ResponseEntity<ProductDTO> updateProduct(
      @RequestBody ProductDTO product, @PathVariable Long productId) {
    ProductDTO productDTO = productService.updateProduct(productId, product);
    return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
  }

  @DeleteMapping("/admin/products/{productId}")
  public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
    ProductDTO productDTO = productService.deleteProduct(productId);
    return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
  }

  @PutMapping("/products/{productId}/image")
  public ResponseEntity<ProductDTO> updateProductImage(
      @PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
    ProductDTO productDTO = productService.updateProductImage(productId, image);
    return new ResponseEntity<ProductDTO>(productDTO, HttpStatus.OK);
  }
}
