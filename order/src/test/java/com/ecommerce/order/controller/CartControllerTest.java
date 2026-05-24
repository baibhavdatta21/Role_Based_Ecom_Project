package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;



    // ==================== ADD TO CART ====================

    @Test
    @DisplayName("Should add item to cart successfully")
    void testAddToCartSuccess() throws Exception {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("1");
        request.setQuantity(2);

        when(cartService.addToCart(anyString(), any(CartItemRequest.class)))
                .thenReturn(true);

        mockMvc.perform(post("/api/auth/cart")
                        .header("X-User-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(cartService, times(1))
                .addToCart(anyString(), any(CartItemRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when add to cart fails")
    void testAddToCartFailure() throws Exception {

        CartItemRequest request = new CartItemRequest();
        request.setProductId("1");
        request.setQuantity(2);

        when(cartService.addToCart(anyString(), any(CartItemRequest.class)))
                .thenReturn(false);

        mockMvc.perform(post("/api/auth/cart")
                        .header("X-User-ID", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Product out of stock or user not found or product not found"
                ));
    }

    // ==================== REMOVE FROM CART ====================

    @Test
    @DisplayName("Should remove item from cart successfully")
    void testRemoveFromCartSuccess() throws Exception {

        when(cartService.deleteItemFromCart("1", 1))
                .thenReturn(true);

        mockMvc.perform(delete("/api/auth/cart/items/1")
                        .header("X-User-ID", "1"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1))
                .deleteItemFromCart("1", 1);
    }

    @Test
    @DisplayName("Should return not found when cart item does not exist")
    void testRemoveFromCartFailure() throws Exception {

        when(cartService.deleteItemFromCart("1", 1))
                .thenReturn(false);

        mockMvc.perform(delete("/api/auth/cart/items/1")
                        .header("X-User-ID", "1"))
                .andExpect(status().isNotFound());
    }

    // ==================== GET CART PRODUCTS ====================

    @Test
    @DisplayName("Should return cart products successfully")
    void testGetCartProductsSuccess() throws Exception {

        CartItem cartItem = new CartItem();
        cartItem.setId(1);
        cartItem.setUserId("1");
        cartItem.setProductId("1");
        cartItem.setQuantity(2);
        cartItem.setPrice(BigDecimal.valueOf(1000));

        when(cartService.getCartProducts("1"))
                .thenReturn(List.of(cartItem));

        mockMvc.perform(get("/api/auth/cart")
                        .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is("1")))
                .andExpect(jsonPath("$[0].productId", is("1")))
                .andExpect(jsonPath("$[0].quantity", is(2)));

        verify(cartService, times(1))
                .getCartProducts("1");
    }

    @Test
    @DisplayName("Should return no content when cart is empty")
    void testGetCartProductsEmpty() throws Exception {

        when(cartService.getCartProducts("1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/auth/cart")
                        .header("X-User-ID", "1"))
                .andExpect(status().isNoContent());
    }
}