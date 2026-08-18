package com.ecommerce.order.service;

import com.ecommerce.order.clients.ProductClient;
import com.ecommerce.order.clients.UserClient;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.exception.DownstreamServiceException;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private UserClient userClient;

    private static final Logger logger= LoggerFactory.getLogger(CartService.class);

    @CircuitBreaker(name="productServ", fallbackMethod = "addToCartFallback")
    public void addToCart(String userId, CartItemRequest request) {
        logger.info("Initiating the addiion of cart items:{} for userid:{}",userId,request);
        ResponseEntity<UserResponse> userResponse=userClient.getUserById(userId);
        if (userResponse.getBody()==null) {
            throw new EntityNotFoundException("No user is present");
        }
        logger.debug("User details received:{}",userResponse);
        Integer integer=Integer.valueOf(request.getProductId());
        ResponseEntity<ProductResponse> productResponse=productClient.getProductById(integer);
        logger.debug("Product details received:{}",productResponse);
        if (productResponse.getBody()==null) {
                    throw new EntityNotFoundException("No such Product is present");
        }
        CartItem existingCartItem=cartItemRepository.findByUserIdAndProductId(userId,request.getProductId());
        if(existingCartItem!=null){
            existingCartItem.setQuantity(existingCartItem.getQuantity()+ request.getQuantity());
            BigDecimal productPrice=productResponse.getBody().getPrice();
            Integer quantity=request.getQuantity();
            BigDecimal currentProductPrice=productPrice.multiply(BigDecimal.valueOf(quantity));
            existingCartItem.setPrice(existingCartItem.getPrice().add(currentProductPrice));
            cartItemRepository.save(existingCartItem);
        }else{
            CartItem cartItem=new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(String.valueOf(request.getProductId()));
            cartItem.setQuantity(request.getQuantity());
            BigDecimal productPrice=productResponse.getBody().getPrice();
            Integer quantity=request.getQuantity();
            BigDecimal currentProductPrice=productPrice.multiply(BigDecimal.valueOf(quantity));
            cartItem.setPrice(currentProductPrice);
            cartItemRepository.save(cartItem);
        }
        logger.info("Cart items added successfully");
    }
    public void addToCartFallback(String userId, CartItemRequest request, Exception e) {
        logger.error("Microservice calls unsuccessful for userId: {}, request: {}", userId, request);
        throw new DownstreamServiceException("User-Service/Product-Service Down",e);
    }
    @Transactional
    public boolean deleteItemFromCart(String userId, Integer productId) {
        logger.info("Initiating the deletion of product id:{} for user id:{}",productId,userId);
        CartItem cartItem=cartItemRepository.findByUserIdAndProductId(userId, String.valueOf(productId));
        if(cartItem!=null){
            cartItemRepository.deleteByUserIdAndProductId(userId, String.valueOf(productId));
            return true;
        }
        return false;
    }

    public List<CartItem> getCartProducts(String userId) {
        logger.info("Initiating the get cart products for userId{}",userId);
        Optional<List<CartItem>> cartItemList=cartItemRepository.findByUserId(userId);
        if(cartItemList.isPresent() && cartItemList.get().size()>0)
        return cartItemList.get();
        else
            return new ArrayList<>();
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
