package com.ecommerce.project.Repository;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
  List<Product> findByCategoryOrderByPriceAsc(Category savedCategory);

  List<Product> findByProductNameLikeIgnoreCase(String keyword);
}
