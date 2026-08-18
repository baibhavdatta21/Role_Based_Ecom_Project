//package com.ecommerce.order.service;
//
//import com.ecommerce.order.clients.ProductClient;
//import com.ecommerce.order.clients.UserClient;
//import com.ecommerce.order.dto.CartItemRequest;
//import com.ecommerce.order.dto.ProductResponse;
//import com.ecommerce.order.dto.UserResponse;
//import com.ecommerce.order.model.CartItem;
//import com.ecommerce.order.repository.CartItemRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartServiceTests {
//
//    @Mock
//    private CartItemRepository cartItemRepository;
//
//    @Mock
//    private ProductClient productClient;
//
//    @Mock
//    private UserClient userClient;
//
//    @InjectMocks
//    private CartService cartService;
//
//    private CartItemRequest cartItemRequest;
//    private CartItem cartItem;
//    private UserResponse userResponse;
//    private ProductResponse productResponse;
//
//    @BeforeEach
//    void setup() {
//
//        ReflectionTestUtils.setField(cartService,
//                "productClient",
//                productClient);
//
//        ReflectionTestUtils.setField(cartService,
//                "userClient",
//                userClient);
//
//        cartItemRequest = new CartItemRequest();
//        cartItemRequest.setProductId("1");
//        cartItemRequest.setQuantity(2);
//
//        cartItem = new CartItem();
//        cartItem.setId(1);
//        cartItem.setUserId("101");
//        cartItem.setProductId("1");
//        cartItem.setQuantity(2);
//        cartItem.setPrice(BigDecimal.valueOf(1000));
//
//        userResponse = new UserResponse();
//        userResponse.setId("101");
//
//        productResponse = new ProductResponse();
//        productResponse.setId(1);
//        productResponse.setName("Laptop");
//    }
//
//    @Nested
//    class AddToCartTests {
//
//        @Test
//        @DisplayName("Should add new item to cart")
//        void addToCart_ShouldAddNewItem() {
//
//            when(userClient.getUserById("101"))
//                    .thenReturn(ResponseEntity.ok(userResponse));
//
//            when(productClient.getProductById(1))
//                    .thenReturn(ResponseEntity.ok(productResponse));
//
//            when(cartItemRepository.findByUserIdAndProductId("101", "1"))
//                    .thenReturn(null);
//
//            boolean result =
//                    cartService.addToCart("101", cartItemRequest);
//
//            assertTrue(result);
//
//            verify(cartItemRepository, times(1))
//                    .save(any(CartItem.class));
//        }
//
//        @Test
//        @DisplayName("Should update existing cart item")
//        void addToCart_ShouldUpdateExistingItem() {
//
//            when(userClient.getUserById("101"))
//                    .thenReturn(ResponseEntity.ok(userResponse));
//
//            when(productClient.getProductById(1))
//                    .thenReturn(ResponseEntity.ok(productResponse));
//
//            when(cartItemRepository.findByUserIdAndProductId("101", "1"))
//                    .thenReturn(cartItem);
//
//            boolean result =
//                    cartService.addToCart("101", cartItemRequest);
//
//            assertTrue(result);
//
//            assertEquals(4, cartItem.getQuantity());
//
//            verify(cartItemRepository, times(1))
//                    .save(cartItem);
//        }
//
//        @Test
//        @DisplayName("Should return false when user not found")
//        void addToCart_ShouldFailWhenUserNotFound() {
//
//            when(userClient.getUserById("101"))
//                    .thenReturn(ResponseEntity.ok(null));
//
//            boolean result =
//                    cartService.addToCart("101", cartItemRequest);
//
//            assertFalse(result);
//
//            verify(cartItemRepository, never())
//                    .save(any());
//        }
//
//        @Test
//        @DisplayName("Should return false when product not found")
//        void addToCart_ShouldFailWhenProductNotFound() {
//
//            when(userClient.getUserById("101"))
//                    .thenReturn(ResponseEntity.ok(userResponse));
//
//            when(productClient.getProductById(1))
//                    .thenReturn(ResponseEntity.ok(null));
//
//            boolean result =
//                    cartService.addToCart("101", cartItemRequest);
//
//            assertFalse(result);
//
//            verify(cartItemRepository, never())
//                    .save(any());
//        }
//    }
//
//    @Nested
//    class DeleteItemTests {
//
//        @Test
//        @DisplayName("Should delete cart item successfully")
//        void deleteItem_ShouldDeleteSuccessfully() {
//
//            when(cartItemRepository.findByUserIdAndProductId("101", "1"))
//                    .thenReturn(cartItem);
//
//            boolean result =
//                    cartService.deleteItemFromCart("101", 1);
//
//            assertTrue(result);
//
//            verify(cartItemRepository, times(1))
//                    .deleteByUserIdAndProductId("101", "1");
//        }
//
//        @Test
//        @DisplayName("Should return false when item not found")
//        void deleteItem_ShouldFailWhenItemNotFound() {
//
//            when(cartItemRepository.findByUserIdAndProductId("101", "1"))
//                    .thenReturn(null);
//
//            boolean result =
//                    cartService.deleteItemFromCart("101", 1);
//
//            assertFalse(result);
//
//            verify(cartItemRepository, never())
//                    .deleteByUserIdAndProductId(any(), any());
//        }
//    }
//
//    @Nested
//    class GetCartProductsTests {
//
//        @Test
//        @DisplayName("Should return cart products")
//        void getCartProducts_ShouldReturnProducts() {
//
//            when(cartItemRepository.findByUserId("101"))
//                    .thenReturn(Optional.of(List.of(cartItem)));
//
//            List<CartItem> result =
//                    cartService.getCartProducts("101");
//
//            assertEquals(1, result.size());
//
//            verify(cartItemRepository, times(1))
//                    .findByUserId("101");
//        }
//
//        @Test
//        @DisplayName("Should return empty list")
//        void getCartProducts_ShouldReturnEmptyList() {
//
//            when(cartItemRepository.findByUserId("101"))
//                    .thenReturn(Optional.empty());
//
//            List<CartItem> result =
//                    cartService.getCartProducts("101");
//
//            assertTrue(result.isEmpty());
//        }
//    }
//
//    @Nested
//    class ClearCartTests {
//
//        @Test
//        @DisplayName("Should clear cart")
//        void clearCart_ShouldDeleteAllItems() {
//
//            cartService.clearCart("101");
//
//            verify(cartItemRepository, times(1))
//                    .deleteByUserId("101");
//        }
//    }
//}