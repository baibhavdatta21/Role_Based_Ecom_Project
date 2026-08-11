package com.ecommerce.order.service;

import com.ecommerce.order.clients.ProductClient;
import com.ecommerce.order.clients.UserClient;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.repository.CartItemRepository;
//import com.example.demo.model.Product;
//import com.example.demo.model.User;
//import com.example.demo.repository.ProductRepositoy;
//import com.example.demo.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
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
    int attempt=0;
//    private final ProductRepositoy productRepositoy;
//    private final UserRepository userRepository;
//    @CircuitBreaker(name="productServ", fallbackMethod = "addToCartFallback")
    @Retry(name="retryBreaker", fallbackMethod = "addToCartFallback")
    public boolean addToCart(String userId, CartItemRequest request) {
//        Optional<Product> productOpt = cartItemRepository.findById(request.getProductId());
//        if (productOpt.isEmpty()) {
//            return false;
//        }
//        Product product = productOpt.get();
//        if(product.getStockQuantity()<request.getQuantity()){
//            return false;
//        }
//        Optional<User> UserOpt=userRepository.findById(Integer.valueOf(userId));
//        if (UserOpt.isEmpty()) {
//            return false;
//        }
//        User user=UserOpt.get();
        System.out.println("Attempt Count: "+ ++attempt);
        ResponseEntity<UserResponse> userResponse=userClient.getUserById(userId);
        if (userResponse.getBody()==null) {
            return false;
        }
        Integer integer=Integer.valueOf(request.getProductId());
        System.out.println("Got the user");
        ResponseEntity<ProductResponse> productResponse=productClient.getProductById(integer);
        System.out.println(productResponse.getStatusCode());
        System.out.println(productResponse.getBody());
                if (productResponse.getBody()==null) {
            return false;
        }
        CartItem existingCartItem=cartItemRepository.findByUserIdAndProductId(userId,request.getProductId());
        if(existingCartItem!=null){
            existingCartItem.setQuantity(existingCartItem.getQuantity()+ request.getQuantity());
            existingCartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(existingCartItem);
        }else{
            CartItem cartItem=new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(String.valueOf(request.getProductId()));
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
        return true;
    }
    public boolean addToCartFallback(String userId, CartItemRequest request, Exception e) {
        System.out.println("Circuit breaker triggered! Fallback called: " + e.getMessage());
        return false;  // Or return cached data
    }
    @Transactional
    public boolean deleteItemFromCart(String userId, Integer productId) {
//        Optional<Product> productOpt = cartItemRepository.findById(productId);
//        if (productOpt.isEmpty()) {
//            return false;
//        }
//        Optional<User> userOpt=userRepository.findById(Integer.valueOf(userId));
//        if (userOpt.isEmpty()) {
//            return false;
//        }
//        Optional<Boolean> b= userOpt.flatMap(user -> productOpt.map(product -> {
//            cartItemRepository.deleteByUserAndProduct(user,product);
//            return true;
//        }));
//        if(b.get())return true;
//        return false;

        CartItem cartItem=cartItemRepository.findByUserIdAndProductId(userId, String.valueOf(productId));
        if(cartItem!=null){
            cartItemRepository.deleteByUserIdAndProductId(userId, String.valueOf(productId));
            return true;
        }
        return false;
    }

    public List<CartItem> getCartProducts(String userId) {
//        Optional<User> userOpt=userRepository.findById(Integer.valueOf(userId));
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
