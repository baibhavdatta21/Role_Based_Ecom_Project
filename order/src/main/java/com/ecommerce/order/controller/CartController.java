package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestHeader("X-User-ID") String userId
                                            ,@RequestBody CartItemRequest request){
        if(!cartService.addToCart(userId,request))
            return ResponseEntity.badRequest().body("Product out of stock or user not found or product not found");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart( @RequestHeader("X-User-ID") String userId
                                                ,@PathVariable Integer productId){
        boolean b=cartService.deleteItemFromCart(userId,productId);
//        System.out.println(b);
        return (b==true)?ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }
    @GetMapping
    public ResponseEntity<?> getCartProducts(@RequestHeader("X-User-ID") String userId){
        List<?>lst=cartService.getCartProducts(userId);
        return (lst.size()==0?ResponseEntity.noContent().build()
                :ResponseEntity.ok().body(lst));
    }

}
