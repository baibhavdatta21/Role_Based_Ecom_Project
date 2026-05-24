package com.ecommerce.order.model;

import com.ecommerce.order.model.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productId;
    private Integer quantity;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name="order_id",nullable = false)
    private Order order;
}
