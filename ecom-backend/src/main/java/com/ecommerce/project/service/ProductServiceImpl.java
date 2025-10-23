package com.ecommerce.project.service;

import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {
  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  CartRepository cartRepository;
  @Autowired ProductRepository productRepository;
  @Autowired ModelMapper modelMapper;
  @Autowired CartService cartService;
  @Value("${image.base.url}")
  private String imageBaseUrl;

  @Override
  public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
    Category savedCategory =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("CategoryName", categoryId, "Category"));
    double specialPrice =
        productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
    productDTO.setSpecialPrice(specialPrice);
    productDTO.setImage("default.png");

    Product product = modelMapper.map(productDTO, Product.class);
    product.setCategory(savedCategory);

    Product savedProduct = productRepository.save(product);

    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductResponse getAllProducts(
          int pageNumber, int pageSize,String sortBy, String sortOrder, String keyword,String category) {

    Sort sortProducts =
        sortOrder.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
    System.out.println("sort++ "+sortProducts+" "+sortOrder);
    Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortProducts);
    Specification<Product> spec = Specification.where(null);
    if(keyword != null  && !keyword.isEmpty()){
      spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
    }
    if (category != null && !category.isEmpty()) {
      spec = spec.and((root, query, criteriaBuilder) ->
              criteriaBuilder.like(root.get("category").get("categoryName"), category));
    }

    Page<Product> products = productRepository.findAll(spec,pageDetails);

    List<ProductDTO> productDTOs=
        products.stream().map((product) -> {ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
          productDTO.setImage(constructImageUrl(productDTO.getImage()));
          return productDTO;
  }).toList();
    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOs);
    productResponse.setPageNumber(products.getNumber());
    productResponse.setPageSize(products.getSize());
    productResponse.setTotalElements(products.getTotalElements());
    productResponse.setTotalPages(products.getTotalPages());
    productResponse.setLastPage(products.isLast());
    return productResponse;
  }


  @Override
  public ProductResponse getProductsByCategory(Long categoryId) {
    Category savedCategory =
        categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("CategoryName", categoryId, "Category"));

    List<Product> products = productRepository.findByCategoryOrderByPriceAsc(savedCategory);
    List<ProductDTO> productDTOs =
        products.stream().map((product) ->{
          ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
          productDTO.setImage(constructImageUrl(productDTO.getImage()));
        return productDTO;
        }).toList();

    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTOs);
    return productResponse;
  }
  private String constructImageUrl(String imageName){
    return imageBaseUrl.endsWith("/")?imageBaseUrl+imageName:imageBaseUrl+"/"+imageName;
  }

  @Override
  public ProductResponse getProductsByKeyword(String keyword) {
    List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
    List<ProductDTO> productDTO =
        products.stream().map((product) -> modelMapper.map(product, ProductDTO.class)).toList();
    ProductResponse productResponse = new ProductResponse();
    productResponse.setContent(productDTO);
    return productResponse;
  }

  @Override
  public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
    Product savedProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductName", productId, "Product"));
    savedProduct.setProductName(productDTO.getProductName());
    savedProduct.setDescription(productDTO.getDescription());
    savedProduct.setQuantity(productDTO.getQuantity());
    savedProduct.setPrice(productDTO.getPrice());
    savedProduct.setDiscount(productDTO.getDiscount());
    double specialPrice =
        productDTO.getPrice() - ((productDTO.getDiscount() * 0.01) * productDTO.getPrice());
    savedProduct.setSpecialPrice(specialPrice);
    savedProduct = productRepository.save(savedProduct);
    List<Cart> carts = cartRepository.findCartByProductId(productId);
    List<CartDTO> cartDTOs =carts.stream().map(cart->{
      CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
      List<ProductDTO>  products = cart.getCartItems().stream().map(p-> modelMapper.map(p,ProductDTO.class)).toList();
      cartDTO.setProducts(products);
      return cartDTO;
    }
    ).toList();
    cartDTOs.forEach(cart-> cartService.updateProductsInCart(cart.getCartId(),productId));
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductDTO deleteProduct(Long productId) {
    Product savedProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductName", productId, "Product"));
    productRepository.delete(savedProduct);
    List<Cart> carts = cartRepository.findCartByProductId(productId);
    carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));
   carts.forEach(cart -> System.out.println(cart.getCartItems().size()));
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
    Product savedProduct =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("ProductName", productId, "Product"));
    String path = "images/";
    String fileName = uploadImage(path, image);
    savedProduct.setImage(fileName);
    savedProduct = productRepository.save(savedProduct);
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  private String uploadImage(String path, MultipartFile file) throws IOException {
    String originalFileName = file.getOriginalFilename();
    String randomId = UUID.randomUUID().toString();
    String fileName =
        randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
    String filePath = Paths.get(path, fileName).toString();
    System.out.println(filePath);
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdir();
    }
    Files.copy(file.getInputStream(), Paths.get(filePath));
    return fileName;
  }
}
