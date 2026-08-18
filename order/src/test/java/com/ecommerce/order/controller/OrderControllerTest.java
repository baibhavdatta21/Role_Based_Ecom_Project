//package com.ecommerce.order.controller;
//
////package com.ecommerce.order.controller;
//
//import com.ecommerce.order.dto.OrderItemDTO;
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.model.OrderStatus;
//import com.ecommerce.order.service.OrderService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(OrderController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class OrderControllerTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private OrderService orderService;
//
//    @Test
//    @DisplayName("Should create order successfully")
//    void testCreateOrderSuccess() throws Exception {
//
//        OrderItemDTO itemDTO = new OrderItemDTO(
//                1,
//                "1",
//                2,
//                BigDecimal.valueOf(1000),
//                BigDecimal.valueOf(2000)
//        );
//
//        OrderResponse response = new OrderResponse(
//                1,
//                BigDecimal.valueOf(2000),
//                OrderStatus.CONFIRMED,
//                List.of(itemDTO),
//                LocalDateTime.now()
//        );
//
//        when(orderService.createOrder("1"))
//                .thenReturn(Optional.of(response));
//
//        mockMvc.perform(post("/api/auth/orders")
//                        .header("X-User-ID", "1"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.status", is("CONFIRMED")))
//                .andExpect(jsonPath("$.totalAmount", is(2000)));
//
//        verify(orderService, times(1))
//                .createOrder("1");
//    }
//
//    @Test
//    @DisplayName("Should return bad request when order creation fails")
//    void testCreateOrderFailure() throws Exception {
//
//        when(orderService.createOrder("1"))
//                .thenReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/auth/orders")
//                        .header("X-User-ID", "1"))
//                .andExpect(status().isBadRequest());
//
//        verify(orderService, times(1))
//                .createOrder("1");
//    }
//}
