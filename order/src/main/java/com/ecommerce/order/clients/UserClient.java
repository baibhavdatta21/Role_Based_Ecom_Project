package com.ecommerce.order.clients;

import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user")
public interface UserClient {
    @GetMapping("/api/auth/users/{id}")
    ResponseEntity<UserResponse> getUserById(@PathVariable("id") String id);
}
