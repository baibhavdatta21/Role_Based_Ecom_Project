//package com.ecommerce.order.service;
//
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.model.CartItem;
//import com.ecommerce.order.model.Order;
//import com.ecommerce.order.model.OrderStatus;
//import com.ecommerce.order.repository.OrderRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTests {
//
//    @Mock
//    private CartService cartService;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private CartItem cartItem1;
//    private CartItem cartItem2;
//
//    @BeforeEach
//    void setup() {
//
//        cartItem1 = new CartItem();
//        cartItem1.setId(1);
//        cartItem1.setUserId("101");
//        cartItem1.setProductId("11");
//        cartItem1.setQuantity(2);
//        cartItem1.setPrice(BigDecimal.valueOf(1000));
//
//        cartItem2 = new CartItem();
//        cartItem2.setId(2);
//        cartItem2.setUserId("101");
//        cartItem2.setProductId("12");
//        cartItem2.setQuantity(1);
//        cartItem2.setPrice(BigDecimal.valueOf(500));
//    }
//
//    @Nested
//    class CreateOrderTests {
//
//        @Test
//        @DisplayName("Should create order successfully")
//        void createOrder_ShouldCreateOrderSuccessfully() {
//
//            List<CartItem> cartItems =
//                    List.of(cartItem1, cartItem2);
//
//            when(cartService.getCartProducts("101"))
//                    .thenReturn(cartItems);
//
//            Order savedOrder = new Order();
//            savedOrder.setId(1);
//            savedOrder.setUserId("101");
//            savedOrder.setStatus(OrderStatus.CONFIRMED);
//            savedOrder.setTotalAmount(BigDecimal.valueOf(1500));
//            savedOrder.setItems(List.of());
//            savedOrder.setCreatedAt(LocalDateTime.now());
//
//            when(orderRepository.save(any(Order.class)))
//                    .thenReturn(savedOrder);
//
//            Optional<OrderResponse> response =
//                    orderService.createOrder("101");
//
//            assertTrue(response.isPresent());
//
//            assertEquals(OrderStatus.CONFIRMED,
//                    response.get().getStatus());
//
//            assertEquals(BigDecimal.valueOf(1500),
//                    response.get().getTotalAmount());
//
//            verify(orderRepository, times(1))
//                    .save(any(Order.class));
//
//            verify(cartService, times(1))
//                    .clearCart("101");
//        }
//
//        @Test
//        @DisplayName("Should calculate total price correctly")
//        void createOrder_ShouldCalculateTotalCorrectly() {
//
//            List<CartItem> cartItems =
//                    List.of(cartItem1, cartItem2);
//
//            when(cartService.getCartProducts("101"))
//                    .thenReturn(cartItems);
//
//            Order savedOrder = new Order();
//            savedOrder.setId(1);
//            savedOrder.setUserId("101");
//            savedOrder.setStatus(OrderStatus.CONFIRMED);
//            savedOrder.setTotalAmount(BigDecimal.valueOf(1500));
//            savedOrder.setItems(List.of());
//            savedOrder.setCreatedAt(LocalDateTime.now());
//
//            when(orderRepository.save(any(Order.class)))
//                    .thenReturn(savedOrder);
//
//            orderService.createOrder("101");
//
//            ArgumentCaptor<Order> orderCaptor =
//                    ArgumentCaptor.forClass(Order.class);
//
//            verify(orderRepository)
//                    .save(orderCaptor.capture());
//
//            Order capturedOrder = orderCaptor.getValue();
//
//            assertEquals(BigDecimal.valueOf(1500),
//                    capturedOrder.getTotalAmount());
//        }
//
//        @Test
//        @DisplayName("Should create correct number of order items")
//        void createOrder_ShouldCreateOrderItems() {
//
//            List<CartItem> cartItems =
//                    List.of(cartItem1, cartItem2);
//
//            when(cartService.getCartProducts("101"))
//                    .thenReturn(cartItems);
//
//            Order savedOrder = new Order();
//            savedOrder.setId(1);
//            savedOrder.setUserId("101");
//            savedOrder.setStatus(OrderStatus.CONFIRMED);
//            savedOrder.setTotalAmount(BigDecimal.valueOf(1500));
//            savedOrder.setCreatedAt(LocalDateTime.now());
//
//            savedOrder.setItems(List.of());
//
//            when(orderRepository.save(any(Order.class)))
//                    .thenReturn(savedOrder);
//
//            orderService.createOrder("101");
//
//            ArgumentCaptor<Order> orderCaptor =
//                    ArgumentCaptor.forClass(Order.class);
//
//            verify(orderRepository)
//                    .save(orderCaptor.capture());
//
//            Order capturedOrder = orderCaptor.getValue();
//
//            assertEquals(2,
//                    capturedOrder.getItems().size());
//        }
//
//        @Test
//        @DisplayName("Should clear cart after order creation")
//        void createOrder_ShouldClearCart() {
//
//            when(cartService.getCartProducts("101"))
//                    .thenReturn(List.of(cartItem1));
//
//            Order savedOrder = new Order();
//            savedOrder.setId(1);
//            savedOrder.setStatus(OrderStatus.CONFIRMED);
//            savedOrder.setTotalAmount(BigDecimal.valueOf(1000));
//            savedOrder.setCreatedAt(LocalDateTime.now());
//
//            when(orderRepository.save(any(Order.class)))
//                    .thenReturn(savedOrder);
//
//            orderService.createOrder("101");
//
//            verify(cartService, times(1))
//                    .clearCart("101");
//        }
//
//        @Test
//        @DisplayName("Should create order even when cart is empty")
//        void createOrder_WithEmptyCart() {
//
//            when(cartService.getCartProducts("101"))
//                    .thenReturn(List.of());
//
//            Order savedOrder = new Order();
//            savedOrder.setId(1);
//            savedOrder.setStatus(OrderStatus.CONFIRMED);
//            savedOrder.setTotalAmount(BigDecimal.ZERO);
//            savedOrder.setCreatedAt(LocalDateTime.now());
//            savedOrder.setItems(List.of());
//
//            when(orderRepository.save(any(Order.class)))
//                    .thenReturn(savedOrder);
//
//            Optional<OrderResponse> response =
//                    orderService.createOrder("101");
//
//            assertTrue(response.isPresent());
//
//            assertEquals(BigDecimal.ZERO,
//                    response.get().getTotalAmount());
//        }
//    }
//}