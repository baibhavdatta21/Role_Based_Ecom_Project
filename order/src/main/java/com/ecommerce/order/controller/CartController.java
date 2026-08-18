package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private static final Logger logger= LoggerFactory.getLogger(CartController.class);
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("X-User-ID") String userId,@Valid @RequestBody CartItemRequest request){
        logger.info("Post request for the cart for userid:{} and cart details:{}, received",userId,request);
        cartService.addToCart(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Cart product added");
    }
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart( @RequestHeader("X-User-ID") String userId,@PathVariable Integer productId){
        logger.info("Delete cart item request received, productId: {}, userId: {}", productId, userId);
        boolean b=cartService.deleteItemFromCart(userId,productId);
        return (b==true)?ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }
    @GetMapping
    public ResponseEntity<?> getCartProducts(@RequestHeader("X-User-ID") String userId){
        logger.info("Request for get all cart products for userid:{}, received", userId);
        List<?>lst=cartService.getCartProducts(userId);
        return (lst.size()==0?ResponseEntity.noContent().build()
                :ResponseEntity.ok().body(lst));
    }

}
