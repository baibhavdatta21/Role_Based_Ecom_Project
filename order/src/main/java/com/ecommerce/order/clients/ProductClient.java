package com.ecommerce.order.clients;

import com.ecommerce.order.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("product")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer id);
}
