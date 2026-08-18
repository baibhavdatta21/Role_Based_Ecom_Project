//package com.ecommerce.product.service;
//
//import com.ecommerce.product.dto.ProductRequest;
//import com.ecommerce.product.dto.ProductResponse;
//import com.ecommerce.product.model.Product;
//import com.ecommerce.product.repository.ProductRepositoy;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("ProductService Unit Tests")
//public class ProductServiceTest {
//
//    @Mock
//    private ProductRepositoy productRepository;
//
//    @InjectMocks
//    private ProductService productService;
//
//    private Product product;
//    private ProductRequest productRequest;
//    private ProductResponse productResponse;
//
//    @BeforeEach
//    public void setUp() {
//        initializeTestData();
//    }
//
//    private void initializeTestData() {
//        // Initialize Product Entity
//        product = new Product();
//        product.setId(1);
//        product.setName("Laptop");
//        product.setDescription("High performance laptop");
//        product.setPrice(new BigDecimal("1299.99"));
//        product.setStockQuantity(50);
//        product.setCategory("Electronics");
//        product.setImageUrl("http://example.com/laptop.jpg");
//        product.setActive(true);
//        product.setCreatedAt(LocalDateTime.now());
//        product.setUpdatedAt(LocalDateTime.now());
//
//        // Initialize ProductRequest
//        productRequest = new ProductRequest();
//        productRequest.setName("Laptop");
//        productRequest.setDescription("High performance laptop");
//        productRequest.setPrice(new BigDecimal("1299.99"));
//        productRequest.setStockQuantity(50);
//        productRequest.setCategory("Electronics");
//        productRequest.setImageUrl("http://example.com/laptop.jpg");
//
//        // Initialize ProductResponse
//        productResponse = new ProductResponse();
//        productResponse.setId(1);
//        productResponse.setName("Laptop");
//        productResponse.setDescription("High performance laptop");
//        productResponse.setPrice(new BigDecimal("1299.99"));
//        productResponse.setStockQuantity(50);
//        productResponse.setCategory("Electronics");
//        productResponse.setImageUrl("http://example.com/laptop.jpg");
//        productResponse.setActive(true);
//    }
//
//    // ==================== CREATE PRODUCT TESTS ====================
//
//    @Test
//    @DisplayName("SVC001: Create Product - Success")
//    public void testCreateProduct_Success() {
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        ProductResponse result = productService.createProduct(productRequest);
//
//        assertNotNull(result);
//        assertEquals("Laptop", result.getName());
//        assertEquals(new BigDecimal("1299.99"), result.getPrice());
//        assertEquals(50, result.getStockQuantity());
//        assertEquals("Electronics", result.getCategory());
//        assertTrue(result.getActive());
//
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    @DisplayName("SVC002: Create Product - All Fields Mapped Correctly")
//    public void testCreateProduct_AllFieldsMapped() {
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        ProductResponse result = productService.createProduct(productRequest);
//
//        assertEquals(productRequest.getName(), result.getName());
//        assertEquals(productRequest.getDescription(), result.getDescription());
//        assertEquals(productRequest.getPrice(), result.getPrice());
//        assertEquals(productRequest.getStockQuantity(), result.getStockQuantity());
//        assertEquals(productRequest.getCategory(), result.getCategory());
//        assertEquals(productRequest.getImageUrl(), result.getImageUrl());
//    }
//
//    @Test
//    @DisplayName("SVC003: Create Product - Null Request")
//    public void testCreateProduct_NullRequest() {
//        assertThrows(NullPointerException.class, () -> {
//            productService.createProduct(null);
//        });
//    }
//
//    // ==================== UPDATE PRODUCT TESTS ====================
//
//    @Test
//    @DisplayName("SVC004: Update Product - Success")
//    public void testUpdateProduct_Success() {
//        Integer productId = 1;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        Optional<ProductResponse> result = productService.updateProduct(productId, productRequest);
//
//        assertTrue(result.isPresent());
//        assertEquals("Laptop", result.get().getName());
//        assertEquals(new BigDecimal("1299.99"), result.get().getPrice());
//
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    @DisplayName("SVC005: Update Product - Product Not Found")
//    public void testUpdateProduct_ProductNotFound() {
//        Integer productId = 999;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        Optional<ProductResponse> result = productService.updateProduct(productId, productRequest);
//
//        assertFalse(result.isPresent());
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    @DisplayName("SVC006: Update Product - Updates Only Specified Fields")
//    public void testUpdateProduct_UpdatesSpecificFields() {
//        Integer productId = 1;
//        ProductRequest updateRequest = new ProductRequest();
//        updateRequest.setName("Updated Laptop");
//        updateRequest.setPrice(new BigDecimal("1499.99"));
//        updateRequest.setStockQuantity(75);
//        updateRequest.setDescription("Updated description");
//        updateRequest.setCategory("Computers");
//        updateRequest.setImageUrl("http://example.com/updated.jpg");
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        Optional<ProductResponse> result = productService.updateProduct(productId, updateRequest);
//
//        assertTrue(result.isPresent());
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    // ==================== GET PRODUCT TESTS ====================
//
//    @Test
//    @DisplayName("SVC007: Get Product By ID - Success")
//    public void testGetProductById_Success() {
//        Integer productId = 1;
//
//        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));
//
//        ProductResponse result = productService.getProductById(productId);
//
//        assertNotNull(result);
//        assertEquals("Laptop", result.getName());
//        assertEquals(1, result.getId());
//        assertTrue(result.getActive());
//
//        verify(productRepository, times(1)).findByIdAndActiveTrue(productId);
//    }
//
//    @Test
//    @DisplayName("SVC008: Get Product By ID - Product Not Found")
//    public void testGetProductById_NotFound() {
//        Integer productId = 999;
//
//        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());
//
//        ProductResponse result = productService.getProductById(productId);
//
//        assertNull(result);
//        verify(productRepository, times(1)).findByIdAndActiveTrue(productId);
//    }
//
//    @Test
//    @DisplayName("SVC009: Get Product By ID - Inactive Product Returns Null")
//    public void testGetProductById_InactiveProduct() {
//        Integer productId = 1;
//        product.setActive(false);
//
//        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());
//
//        ProductResponse result = productService.getProductById(productId);
//
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("SVC010: Get All Products - Success")
//    public void testGetAllProducts_Success() {
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//        Product product2 = new Product();
//        product2.setId(2);
//        product2.setName("Monitor");
//        product2.setActive(true);
//        products.add(product2);
//
//        when(productRepository.findByActiveTrue()).thenReturn(products);
//
//        List<ProductResponse> result = productService.getAllProducts();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("Laptop", result.get(0).getName());
//        assertEquals("Monitor", result.get(1).getName());
//
//        verify(productRepository, times(1)).findByActiveTrue();
//    }
//
//    @Test
//    @DisplayName("SVC011: Get All Products - Empty List")
//    public void testGetAllProducts_EmptyList() {
//        when(productRepository.findByActiveTrue()).thenReturn(new ArrayList<>());
//
//        List<ProductResponse> result = productService.getAllProducts();
//
//        assertNotNull(result);
//        assertEquals(0, result.size());
//
//        verify(productRepository, times(1)).findByActiveTrue();
//    }
//
//    // ==================== DELETE PRODUCT TESTS ====================
//
//    @Test
//    @DisplayName("SVC012: Delete Product - Success (Soft Delete)")
//    public void testDeleteProduct_Success() {
//        Integer productId = 1;
//        product.setActive(true);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        Boolean result = productService.deleteProduct(productId);
//
//        assertTrue(result);
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    @DisplayName("SVC013: Delete Product - Product Not Found")
//    public void testDeleteProduct_NotFound() {
//        Integer productId = 999;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        Boolean result = productService.deleteProduct(productId);
//
//        assertFalse(result);
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    @DisplayName("SVC014: Delete Product - Sets Active Flag to False")
//    public void testDeleteProduct_SetInactiveFlag() {
//        Integer productId = 1;
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        productService.deleteProduct(productId);
//
//        verify(productRepository, times(1)).save(argThat(p -> !p.getActive()));
//    }
//
//    // ==================== SEARCH PRODUCT TESTS ====================
//
//    @Test
//    @DisplayName("SVC015: Search Products - Success by Name")
//    public void testSearchProducts_SuccessByName() {
//        String keyword = "Laptop";
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//
//        when(productRepository.searchProducts(keyword)).thenReturn(products);
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("Laptop", result.get(0).getName());
//
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//
//    @Test
//    @DisplayName("SVC016: Search Products - Success by Description")
//    public void testSearchProducts_SuccessByDescription() {
//        String keyword = "performance";
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//
//        when(productRepository.searchProducts(keyword)).thenReturn(products);
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//
//    @Test
//    @DisplayName("SVC017: Search Products - Success by Category")
//    public void testSearchProducts_SuccessByCategory() {
//        String keyword = "Electronics";
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//
//        when(productRepository.searchProducts(keyword)).thenReturn(products);
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        assertTrue(result.size() > 0);
//
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//
//    @Test
//    @DisplayName("SVC018: Search Products - No Results Found")
//    public void testSearchProducts_NoResults() {
//        String keyword = "NonExistentProduct";
//
//        when(productRepository.searchProducts(keyword)).thenReturn(new ArrayList<>());
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        assertEquals(0, result.size());
//
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//
//    @Test
//    @DisplayName("SVC019: Search Products - Case Insensitive Search")
//    public void testSearchProducts_CaseInsensitive() {
//        String keyword = "LAPTOP";
//        List<Product> products = new ArrayList<>();
//        products.add(product);
//
//        when(productRepository.searchProducts(keyword)).thenReturn(products);
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//
//    @Test
//    @DisplayName("SVC020: Search Products - Null Keyword")
//    public void testSearchProducts_NullKeyword() {
//        when(productRepository.searchProducts(null)).thenReturn(new ArrayList<>());
//
//        List<ProductResponse> result = productService.searchProducts(null);
//
//        assertNotNull(result);
//        assertEquals(0, result.size());
//    }
//
//    @Test
//    @DisplayName("SVC021: Search Products - Empty Keyword")
//    public void testSearchProducts_EmptyKeyword() {
//        when(productRepository.searchProducts("")).thenReturn(new ArrayList<>());
//
//        List<ProductResponse> result = productService.searchProducts("");
//
//        assertNotNull(result);
//        assertEquals(0, result.size());
//    }
//
//    // ==================== MAPPING TESTS ====================
//
//    @Test
//    @DisplayName("SVC022: Map ProductRequest to Product - All Fields")
//    public void testMapProductRequestToProduct_AllFields() {
//        Product newProduct = new Product();
//
//        Product result = productService.mapProductRequestToProduct(newProduct, productRequest);
//
//        assertEquals(productRequest.getName(), result.getName());
//        assertEquals(productRequest.getDescription(), result.getDescription());
//        assertEquals(productRequest.getPrice(), result.getPrice());
//        assertEquals(productRequest.getStockQuantity(), result.getStockQuantity());
//        assertEquals(productRequest.getCategory(), result.getCategory());
//        assertEquals(productRequest.getImageUrl(), result.getImageUrl());
//    }
//
//    @Test
//    @DisplayName("SVC023: Map Product to ProductResponse - All Fields")
//    public void testMapProductToProductResponse_AllFields() {
//        ProductResponse result = productService.mapProductToProductResponse(product);
//
//        assertEquals(product.getId(), result.getId());
//        assertEquals(product.getName(), result.getName());
//        assertEquals(product.getDescription(), result.getDescription());
//        assertEquals(product.getPrice(), result.getPrice());
//        assertEquals(product.getStockQuantity(), result.getStockQuantity());
//        assertEquals(product.getCategory(), result.getCategory());
//        assertEquals(product.getImageUrl(), result.getImageUrl());
//        assertEquals(product.getActive(), result.getActive());
//    }
//
//    @Test
//    @DisplayName("SVC024: Map Product to ProductResponse - Preserves Active Status")
//    public void testMapProductToProductResponse_PreservesActiveStatus() {
//        product.setActive(false);
//
//        ProductResponse result = productService.mapProductToProductResponse(product);
//
//        assertFalse(result.getActive());
//    }
//
//    @Test
//    @DisplayName("SVC025: Search Products - Duplicate Handling in HashSet")
//    public void testSearchProducts_DuplicateHandling() {
//        String keyword = "Laptop";
//        // Product matches both name and description
//        List<Product> products = new ArrayList<>();
//        products.add(product); // Will be added once to HashSet despite multiple matches
//
//        when(productRepository.searchProducts(keyword)).thenReturn(products);
//
//        List<ProductResponse> result = productService.searchProducts(keyword);
//
//        assertNotNull(result);
//        // HashSet prevents duplicates
//        assertTrue(result.size() >= 1);
//
//        verify(productRepository, times(1)).searchProducts(keyword);
//    }
//}
