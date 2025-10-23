package com.ecommerce.project.service;

import com.ecommerce.project.Repository.CartItemRepository;
import com.ecommerce.project.Repository.CartRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.util.AuthUtil;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Stream;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {
  @Autowired CartRepository cartRepository;
  @Autowired AuthUtil authUtil;
  @Autowired ProductRepository productRepository;
  @Autowired CartItemRepository cartItemRepository;
  @Autowired ModelMapper modelMapper;


  @Override
  public CartDTO addProductToCart(Long productId, Integer quantity) {
    Cart cart = createCart();
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("productId", productId, "Product"));
    CartItem cartItem =
        cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
    if (cartItem != null) {
      throw new APIException("Product " + product.getProductName() + " already exists in the cart");
    }
    if (product.getQuantity() == 0) {
      throw new APIException(product.getProductName() + "is not available");
    }
    if (product.getQuantity() < quantity) {
      throw new APIException(
          "Please, make an order of the "
              + product.getProductName()
              + " less than or equal to the quantity "
              + product.getQuantity());
    }

    CartItem newCartItem = new CartItem();
    newCartItem.setProduct(product);
    newCartItem.setCart(cart);
    newCartItem.setQuantity(quantity);
    newCartItem.setDiscount(product.getDiscount());
    newCartItem.setProductPrice(product.getSpecialPrice());
    cartItemRepository.save(newCartItem);
    product.setQuantity(product.getQuantity());
    cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
    cart.getCartItems().add(newCartItem);
    cartRepository.save(cart);
    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

    List<CartItem> cartItems = cart.getCartItems();

    Stream<ProductDTO> productStream =
        cartItems.stream()
            .map(
                item -> {
                  ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
                  map.setQuantity(item.getQuantity());
                  return map;
                });

    cartDTO.setProducts(productStream.toList());

    return cartDTO;
  }

  @Override
  public List<CartDTO> getAllCarts() {
    List<Cart> carts = cartRepository.findAll();
    if(carts.isEmpty()){
      throw new APIException("No cart exist");
    }
    List<CartDTO> cartDTOs = carts.stream().map(cart -> {
      CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
      List<ProductDTO> products = cart.getCartItems()== null? new ArrayList<>():cart.getCartItems().stream().map(p->{ProductDTO productDTO = modelMapper.map(p.getProduct(),ProductDTO.class);
        productDTO.setQuantity(p.getQuantity());
        return productDTO;
      }).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }).toList();
    return cartDTOs;
  }

  @Override
  public CartDTO getCart(String emailId, Long cartId) {
    Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
    if(cart == null){
        throw new ResourceNotFoundException("cartId", cartId, "Cart");
    }
    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);


    List<ProductDTO> products = cart.getCartItems()== null? new ArrayList<>():cart.getCartItems().stream().map(p->{ProductDTO productDTO = modelMapper.map(p.getProduct(),ProductDTO.class);
    productDTO.setQuantity(p.getQuantity());
    return productDTO;
    }).toList();

    cartDTO.setProducts(products);
    return cartDTO;
  }

  @Override
  @Transactional
  public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

    User user = authUtil.loggedInUser();
    Cart cart = cartRepository.findById(user.getCart().getCartId())
            .orElseThrow(() -> new ResourceNotFoundException("cartId", user.getCart().getCartId(), "Cart"));


    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("productId", productId, "Product"));


    if (product.getQuantity() == 0) {
      throw new APIException(product.getProductName() + " is not available");
    }
    if (product.getQuantity() < quantity) {
      throw new APIException("Please order " + product.getProductName() +
              " with quantity <= " + product.getQuantity());
    }


    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
    if (cartItem == null) {
      throw new APIException("Product " + product.getProductName() + " is not available in the cart");
    }


    int newQuantity = cartItem.getQuantity() + quantity;
    if (newQuantity < 0) {
      throw new APIException("The resulting quantity cannot be negative");
    }

    if (newQuantity == 0) {

      cart.setTotalPrice(cart.getTotalPrice() -
              (cartItem.getProductPrice() * cartItem.getQuantity()));
      cart.getCartItems().remove(cartItem);
      product.getProducts().remove(cartItem);

    } else {
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setQuantity(newQuantity);
      cartItem.setDiscount(product.getDiscount());
      cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
    }
    Cart updatedCart = cartRepository.save(cart);

    CartDTO cartDTO = modelMapper.map(updatedCart, CartDTO.class);
    List<ProductDTO> products = updatedCart.getCartItems() == null
            ? new ArrayList<>()
            : updatedCart.getCartItems().stream().map(p -> {
      ProductDTO productDTO = modelMapper.map(p.getProduct(), ProductDTO.class);
      productDTO.setQuantity(p.getQuantity());
      return productDTO;
    }).toList();

    cartDTO.setProducts(products);

    return cartDTO;
  }

  @Override
  @Transactional
  public String deleteProductFromCart(Long cartId, Long productId) {
    Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("cartId",cartId, "Cart"));
    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
    if(cartItem== null){
      throw new ResourceNotFoundException("productId", productId,"Product");
    }
    cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
    cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

    return "Product "+cartItem.getProduct().getProductName()+" removed from the cart !!!";
  }

  @Override
  public void updateProductsInCart(Long cartId, Long productId) {
    Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("cartId",cartId, "Cart"));
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("productId", productId, "Product"));
    CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
    if(cartItem== null){
      throw new APIException("Product" +product.getProductName()+ " not available in the cart !!!");
    }
    double cartPrice = cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
    cartItem.setProductPrice(product.getSpecialPrice());
    cart.setTotalPrice(cartPrice+cartItem.getProductPrice()*cartItem.getQuantity());
    cartItemRepository.save(cartItem);
    //cartRepository.save(cart);
  }
  @Transactional
  @Override
  public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
    // Get user's email
    String emailId = authUtil.loggedInEmail();

    // Check if an existing cart is available or create a new one
    Cart existingCart = cartRepository.findCartByEmail(emailId);
    if (existingCart == null) {
      existingCart = new Cart();
      existingCart.setTotalPrice(0.00);
      existingCart.setUser(authUtil.loggedInUser());
      existingCart = cartRepository.save(existingCart);
    } else {
      // Clear all current items in the existing cart
      cartItemRepository.deleteAllByCartId(existingCart.getCartId());
    }

    double totalPrice = 0.00;

    // Process each item in the request to add to the cart

    for (CartItemDTO cartItemDTO : cartItems) {
      Long productId = cartItemDTO.getProductId();
      Integer quantity = cartItemDTO.getQuantity();

      // Find the product by ID
      Product product = productRepository.findById(productId)
              .orElseThrow(() -> new ResourceNotFoundException( "productId", productId,"Product"));


      totalPrice += product.getSpecialPrice() * quantity;

      // Create and save cart item
      CartItem cartItem = new CartItem();
      cartItem.setProduct(product);
      cartItem.setCart(existingCart);
      cartItem.setQuantity(quantity);
      cartItem.setProductPrice(product.getSpecialPrice());
      cartItem.setDiscount(product.getDiscount());
      cartItemRepository.save(cartItem);
    }

    // Update the cart's total price and save
    existingCart.setTotalPrice(totalPrice);
    cartRepository.save(existingCart);
    return "Cart created/updated with the new items successfully";
  }



  private Cart createCart() {
    Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
    if (userCart != null) {
      return userCart;
    }
    Cart cart = new Cart();
    cart.setTotalPrice(0.00);
    cart.setUser(authUtil.loggedInUser());
    Cart newCart = cartRepository.save(cart);
    return newCart;
  }



}
