package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Test Class for ProductController
 * Tests REST API endpoints for Product operations
 *
 * @author QA Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController Unit Tests")
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        initializeTestData();
    }

    private void initializeTestData() {
        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("99.99"));
        productRequest.setStockQuantity(100);
        productRequest.setCategory("Electronics");
        productRequest.setImageUrl("http://example.com/image.jpg");

        productResponse = new ProductResponse();
        productResponse.setId(1);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(new BigDecimal("99.99"));
        productResponse.setStockQuantity(100);
        productResponse.setCategory("Electronics");
        productResponse.setImageUrl("http://example.com/image.jpg");
        productResponse.setActive(true);
    }

    @Test
    @DisplayName("TC001: Create Product - Success")
    public void testCreateProduct_Success() throws Exception {
        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/auth/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.active").value(true));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC002: Create Product - Invalid Request Body")
    public void testCreateProduct_InvalidRequest() throws Exception {
        String invalidRequest = "{}";

        mockMvc.perform(post("/api/auth/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC003: Update Product - Success")
    public void testUpdateProduct_Success() throws Exception {
        Integer productId = 1;

        when(productService.updateProduct(eq(productId), any(ProductRequest.class)))
                .thenReturn(Optional.of(productResponse));

        mockMvc.perform(put("/api/auth/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC004: Update Product - Product Not Found")
    public void testUpdateProduct_NotFound() throws Exception {
        Integer productId = 999;

        when(productService.updateProduct(eq(productId), any(ProductRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/auth/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(productId), any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC005: Get Product By ID - Success")
    public void testGetProductById_Success() throws Exception {
        Integer productId = 1;

        when(productService.getProductById(productId))
                .thenReturn(productResponse);

        mockMvc.perform(get("/api/auth/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.category").value("Electronics"));

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    @DisplayName("TC006: Get Product By ID - Product Not Found")
    public void testGetProductById_NotFound() throws Exception {
        Integer productId = 999;

        when(productService.getProductById(productId))
                .thenReturn(null);

        mockMvc.perform(get("/api/auth/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    @DisplayName("TC007: Delete Product - Success")
    public void testDeleteProduct_Success() throws Exception {
        Integer productId = 1;

        when(productService.deleteProduct(productId))
                .thenReturn(true);

        mockMvc.perform(delete("/api/auth/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    @DisplayName("TC008: Delete Product - Product Not Found")
    public void testDeleteProduct_NotFound() throws Exception {
        Integer productId = 999;

        when(productService.deleteProduct(productId))
                .thenReturn(false);

        mockMvc.perform(delete("/api/auth/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    @DisplayName("TC009: Search Products - Success")
    public void testSearchProducts_Success() throws Exception {
        String keyword = "Electronics";
        List<ProductResponse> searchResults = new ArrayList<>();
        searchResults.add(productResponse);

        when(productService.searchProducts(keyword))
                .thenReturn(searchResults);

        mockMvc.perform(get("/api/public/products/search")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).searchProducts(keyword);
    }

    @Test
    @DisplayName("TC010: Search Products - No Results Found")
    public void testSearchProducts_NoResults() throws Exception {
        String keyword = "NonExistent";

        when(productService.searchProducts(keyword))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/public/products/search")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).searchProducts(keyword);
    }

    @Test
    @DisplayName("TC011: Search Products - Empty Keyword")
    public void testSearchProducts_EmptyKeyword() throws Exception {
        when(productService.searchProducts(""))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/public/products/search")
                        .param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk());

        verify(productService, times(1)).searchProducts("");
    }

    @Test
    @DisplayName("TC012: Search Products - Special Characters")
    public void testSearchProducts_SpecialCharacters() throws Exception {
        String keyword = "Test@#$%";

        when(productService.searchProducts(keyword))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/public/products/search")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).searchProducts(keyword);
    }

    @Test
    @DisplayName("TC013: Create Product - Null Name")
    public void testCreateProduct_NullName() throws Exception {
        productRequest.setName(null);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/auth/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC014: Create Product - Negative Price")
    public void testCreateProduct_NegativePrice() throws Exception {
        productRequest.setPrice(new BigDecimal("-99.99"));

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/auth/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("TC015: Create Product - Negative Stock")
    public void testCreateProduct_NegativeStock() throws Exception {
        productRequest.setStockQuantity(-10);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(productResponse);

        mockMvc.perform(post("/api/auth/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }
}
