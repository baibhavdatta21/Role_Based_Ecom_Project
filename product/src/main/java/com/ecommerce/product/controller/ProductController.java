package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private static final Logger logger= LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    @PostMapping("/api/auth/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
        logger.info("Post request for Product addition received, product details:{}",productRequest);
        return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }
    @PutMapping("/api/auth/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Integer id,@Valid @RequestBody ProductRequest productRequest){
        logger.info("Put request for Product updation received, product details:{}",productRequest);
        return productService.updateProduct(id, productRequest).map(ResponseEntity:: ok)
                .orElseGet(()->ResponseEntity.notFound().build());
    }
    @GetMapping("/api/public/products")
    public ResponseEntity<List<ProductResponse>> getProducts(){
        logger.info("Get request for all products received");
        return new ResponseEntity<List<ProductResponse>>(productService.getAllProducts(), HttpStatus.OK);
    }
    @GetMapping("/api/auth/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") Integer id){
        logger.info("Get request for Product with Product Id:{}, received",id);
        ProductResponse productResponse=productService.getProductById(id);
        if(productResponse!=null){
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        }
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
    @DeleteMapping("/api/auth/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id){
        logger.info("Delete request for Product deletion received, Product Id:{}",id);
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("api/public/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword){
        logger.info("Get request for searching products received");
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
