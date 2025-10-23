package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

  ProductDTO addProduct(Long categoryId, ProductDTO product);

  ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder, String keyword, String category);

  ProductResponse getProductsByCategory(Long categoryId);

  ProductResponse getProductsByKeyword(String keyword);

  ProductDTO updateProduct(Long productId, ProductDTO product);

  ProductDTO deleteProduct(Long productId);

  ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
