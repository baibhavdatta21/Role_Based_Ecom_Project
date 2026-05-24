package api.gateway.security;

import api.gateway.exception.BadRequestException;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    @Autowired
    Validator validator;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    Map<String, String> endpointMethodRoleMapping;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            System.out.println("inside Gateway Filter");

            if (validator.predicate.test(exchange.getRequest())) {
                System.out.println("valid endpoint checking");

                // Check if Authorization header exists
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new BadRequestException("Authorization Header is missing", HttpStatus.UNAUTHORIZED);
                }

                // Extract token from Authorization header
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String token = null;

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                try {
                    // Validate token signature and expiration
                    jwtUtil.validateToken(token);
                    System.out.println("Token validated successfully");

                    // Get request path and method for role-based authorization check
                    String requestPath = exchange.getRequest().getURI().getPath();
                    String httpMethod = exchange.getRequest().getMethod().toString();
                    System.out.println("Request path: " + requestPath + ", Method: " + httpMethod);

                    // Check if this endpoint+method combination requires specific roles
                    String requiredRoles = getRequiredRolesForEndpointMethod(requestPath, httpMethod);

                    if (requiredRoles != null) {
                        // Check if user has any of the required roles
                        List<String> rolesNeeded = Arrays.asList(requiredRoles.split(","));
                        boolean hasRequiredRole = jwtUtil.hasAnyRole(token, rolesNeeded);

                        System.out.println("Required roles: " + requiredRoles + ", User has required role: " + hasRequiredRole);

                        if (!hasRequiredRole) {
                            throw new BadRequestException(
                                    "Access Denied: Required roles are " + requiredRoles,
                                    HttpStatus.FORBIDDEN
                            );
                        }
                        System.out.println("User authorized for " + httpMethod + " " + requestPath);
                    }

                } catch (BadRequestException ex) {
                    // Re-throw BadRequestException as is (for access denied, missing auth, etc.)
                    throw ex;
                } catch (Exception ex) {
                    // Any other exception means invalid token
                    throw new BadRequestException("Invalid Token", HttpStatus.UNAUTHORIZED);
                }
            }
            return chain.filter(exchange);
        };
    }

    /**
     * Get required roles for endpoint+method combination
     * Returns comma-separated roles (e.g., "CUSTOMER,SELLER") if authorization needed
     * Returns null if no role is required (publicly accessible)
     */
    private String getRequiredRolesForEndpointMethod(String requestPath, String httpMethod) {
        // Construct the key as METHOD:/path
        String methodPathKey = httpMethod + ":" + requestPath;

        // Check for exact match first
        if (endpointMethodRoleMapping.containsKey(methodPathKey)) {
            return endpointMethodRoleMapping.get(methodPathKey);
        }

        // Check for pattern match (with wildcards like /api/products/**)
        for (Map.Entry<String, String> entry : endpointMethodRoleMapping.entrySet()) {
            String pattern = entry.getKey();

            // Extract method and path from pattern key
            if (pattern.startsWith(httpMethod + ":")) {
                String pathPattern = pattern.substring((httpMethod + ":").length());

                if (antPathMatcher.match(pathPattern, requestPath)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public static class Config {

    }
}
