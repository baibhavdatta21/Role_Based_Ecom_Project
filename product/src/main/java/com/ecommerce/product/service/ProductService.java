package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepositoy;
import com.ecommerce.product.util.KMPAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepositoy productRepository;
    public Product mapProductRequestToProduct(Product product, ProductRequest productRequest){

        product.setName(productRequest.getName());
        product.setCategory(productRequest.getCategory());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
        return product;

    }
    public ProductResponse mapProductToProductResponse(Product product){
        ProductResponse productResponse=new ProductResponse();
        productResponse.setName(product.getName());
        productResponse.setCategory(product.getCategory());
        productResponse.setPrice(product.getPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setId(product.getId());
        productResponse.setActive(product.getActive());
        return productResponse;
    }
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product=new Product();
        mapProductRequestToProduct(product, productRequest);
        productRepository.save(product);
        return mapProductToProductResponse(product);
    }

    public Optional<ProductResponse> updateProduct(Integer id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct-> {
            mapProductRequestToProduct(existingProduct, productRequest);
            Product savedProduct=productRepository.save(existingProduct);
            return mapProductToProductResponse(savedProduct);
        });
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products=productRepository.findByActiveTrue();
        return products.stream().map(product->mapProductToProductResponse(product)).collect(Collectors.toList());
    }
    public Boolean deleteProduct(Integer id){
        return productRepository.findById(id).map(product -> {
            product.setActive(false);
            productRepository.save(product);
            return  true;
        }).orElse( false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> prd=productRepository.searchProducts(keyword);
        Set<Product> ans = new HashSet<>();
        for(int i=0;i<prd.size();i++) {
            Product dum=prd.get(i);
            String prodName=dum.getName();
            if (KMPAlgorithm.kmpSearch(prodName.toLowerCase(), keyword.toLowerCase()) != -1) {
                ans.add(dum);
            }
            String prodDesc=dum.getDescription();
            if (KMPAlgorithm.kmpSearch(prodDesc.toLowerCase(), keyword.toLowerCase()) != -1) {
                ans.add(dum);
            }
            String prodCategory=dum.getCategory();
            if (KMPAlgorithm.kmpSearch(prodCategory.toLowerCase(), keyword.toLowerCase()) != -1) {
                ans.add(dum);
            }
        }
        return ans.stream().map(existing->mapProductToProductResponse(existing)).collect(Collectors.toList());
    }

    public ProductResponse getProductById(Integer id) {
        Optional<Product> optionalProduct=productRepository.findByIdAndActiveTrue(id);
        if(optionalProduct.isPresent()){
            return mapProductToProductResponse(optionalProduct.get());
        }
        return null;
    }
}
