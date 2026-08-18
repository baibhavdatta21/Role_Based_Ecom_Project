package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotBlank(message = "ProductId is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Must be a positive number")
    private Integer quantity;
}
