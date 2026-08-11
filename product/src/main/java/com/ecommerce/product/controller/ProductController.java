package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    @PostMapping("/api/auth/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
        return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }
    @PutMapping("/api/auth/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest productRequest){
        return productService.updateProduct(id, productRequest).map(ResponseEntity:: ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }
    @GetMapping("/api/products")
    public ResponseEntity<List<ProductResponse>> createProduct(){
        return new ResponseEntity<List<ProductResponse>>(productService.getAllProducts(), HttpStatus.OK);
    }
    @GetMapping("/api/auth/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer id){
        ProductResponse productResponse=productService.getProductById(id);
        if(productResponse!=null){
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        }
        return new ResponseEntity<>(null,HttpStatus.OK);
    }
    @DeleteMapping("/api/auth/products/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Integer id){
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }
    @GetMapping("api/public/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword){
        return new ResponseEntity<List<ProductResponse>>(productService.searchProducts(keyword), HttpStatus.OK);
    }

    @GetMapping("/api/public/products/simulate/failure")
    public ResponseEntity<String> simulateFailure(@RequestParam(defaultValue = "false") boolean fail){
        if(fail){
            throw new RuntimeException("Simulated Failure for Testing");
        }
        return ResponseEntity.ok("Product Service is OK");
    }
}
