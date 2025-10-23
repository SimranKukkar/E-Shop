package com.ecommerce.project.model;

import com.ecommerce.project.model.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long cartItemId;

    @ManyToOne()
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
