package com.ecommerce.project.service;

import com.ecommerce.project.Repository.*;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderItemDTO;
import com.ecommerce.project.payload.OrderResponse;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
       Cart cart = cartRepository.findCartByEmail(emailId);
       if(cart == null){
           throw new ResourceNotFoundException("email",emailId,"Cart");
       }
       Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("addressId",addressId,"Address"));
       Order order = new Order();
       order.setEmail(emailId);
       order.setOrderDate(LocalDate.now());
       order.setTotalAmount(cart.getTotalPrice());
       order.setOrderStatus("Order Accepted !");
      
       order.setAddress(address);
       Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus,pgResponseMessage,pgName);
       payment.setOrder(order);
       payment = paymentRepository.save(payment);
       order.setPayment(payment);
       Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("cart is empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
           OrderItem orderItem = new OrderItem();
           orderItem.setOrder(savedOrder);
           orderItem.setProduct(cartItem.getProduct());
           orderItem.setQuantity(cartItem.getQuantity());
           orderItem.setDiscount(cartItem.getDiscount());
           orderItem.setOrderedProductPrice(cartItem.getProductPrice());
           orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);
        cartItems.forEach(item->{
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);
            cartService.deleteProductFromCart(item.getCart().getCartId(), item.getProduct().getProductId());

        });
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item-> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));
        return orderDTO;
    }
    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());
        return orderResponse;
    }
}
