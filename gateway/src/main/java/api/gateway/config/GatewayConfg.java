package api.gateway.config;

import api.gateway.security.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfg {
    @Autowired
    private AuthFilter authFilter;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Product Service
                .route("product", r -> r
                        .path("/api/auth/products/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://product")
                )
                .route("product", r -> r
                        .path("/api/public/products/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://product")
                )
                // User Service
                .route("user", r -> r
                        .path("/api/public/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://user")
                )
                .route("user", r -> r
                        .path("/api/auth/users/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://user")
                )
                // Order Service
                .route("order", r -> r
                        .path("/api/auth/order/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://order")
                )
                // Cart Service
                .route("cart", r -> r
                        .path("/api/auth/cart/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthFilter.Config())))
                        .uri("lb://order")
                )
                .build();
    }
}
