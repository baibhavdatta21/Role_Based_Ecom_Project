package api.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RoleAuthorizationConfig {
    @Bean
    public Map<String, String> endpointMethodRoleMapping() {
        Map<String, String> mapping = new HashMap<>();

        // ==================== PRODUCTS ENDPOINT ====================
        mapping.put("GET:/api/products/**","ADMIN,CUSTOMER,SELLER");
        mapping.put("POST:/api/products/**", "ADMIN,SELLER");
        mapping.put("PUT:/api/products/**", "ADMIN,SELLER");
        mapping.put("DELETE:/api/products/**", "ADMIN,SELLER");

        // ==================== CART ENDPOINT ====================
        mapping.put("GET:/api/cart/**", "ADMIN,CUSTOMER");
        mapping.put("POST:/api/cart/**", "ADMIN,CUSTOMER");
        mapping.put("DELETE:/api/cart/**", "ADMIN,CUSTOMER");

        // ==================== ORDER ENDPOINT ====================
        mapping.put("GET:/api/order/**", "ADMIN,CUSTOMER");
//        mapping.put("POST:/api/order/**", "ADMIN,CUSTOMER");
//        mapping.put("PUT:/api/order/**", "ADMIN,CUSTOMER");

        // ==================== USERS ENDPOINT ====================
        mapping.put("GET:/api/auth/users/**", "ADMIN");
        mapping.put("DELETE:/api/auth/users/**", "ADMIN,CUSTOMER,SELLER");
        mapping.put("POST:/api/users/signup-admin", "ADMIN");
        mapping.put("PUT:/api/users", "ADMIN,CUSTOMER,SELLER");
        mapping.put("DELETE:/api/auth/users/","ADMIN,CUSTOMER,SELLER");
        return mapping;
    }
}
