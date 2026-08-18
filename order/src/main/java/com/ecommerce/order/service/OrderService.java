package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
//import com.example.demo.model.*;
//import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private static Logger logger= LoggerFactory.getLogger(OrderService.class);
    public OrderResponse createOrder(String userId) {
        logger.info("Initiating the cart creation for user Id:{}",userId);
        List<CartItem> cartItems=cartService.getCartProducts(userId);
        BigDecimal totalPrice=cartItems.stream()
                .map(m->m.getPrice())
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        Order order=new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem>orderItems=cartItems.stream()
                .map(item->
                        new OrderItem(
                                null,
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice(),
                                order
                        )).toList();
        order.setItems(orderItems);
        Order savedOrder=orderRepository.save(order);
        cartService.clearCart(userId);
        return mapToOrderResponse(savedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        logger.debug("Initiating the mapping response for order");
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream().map(orderItem -> new OrderItemDTO(
                        orderItem.getId(),
                        orderItem.getProductId(),
                        orderItem.getQuantity(),
                        orderItem.getPrice(),
                        orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                )).toList(),order.getCreatedAt()
        );
    }
}
