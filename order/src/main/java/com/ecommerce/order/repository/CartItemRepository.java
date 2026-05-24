package com.ecommerce.order.repository;

import com.ecommerce.order.model.CartItem;
//import com.example.demo.model.Product;
//import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    CartItem findByUserIdAndProductId(String userId, String productId);

    void deleteByUserIdAndProductId(String userId, String productId);

    Optional<List<CartItem>> findByUserId(String userId);

    void deleteByUserId(String userId);
}
