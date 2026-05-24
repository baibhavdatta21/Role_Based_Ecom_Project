package api.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String secretKey =
            "Iax5+gIYQsITpUJLZLLMMKeAhnDzo6S6y9DUnlld6F4kB89cG80ZlJpX3vXTSO8a9vvzvwkN3C+8SZryP17ESQ==";

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Extract all roles from JWT token
     * Assumes the token has a "roles" claim which is a list of objects with "authority" field
     * Format: { "roles": [{ "authority": "ADMIN" }, { "authority": "CUSTOMER" }] }
     * @return List of role authorities
     */
    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);

            // Get the roles list from claims
            List<Map<String, String>> roles = claims.get("roles", List.class);

            // Check if roles list exists and is not empty
            if (roles != null && !roles.isEmpty()) {
                List<String> authorities = new ArrayList<>();

                // Loop through all roles and extract authorities
                for (Map<String, String> role : roles) {
                    String authority = role.get("authority");
                    if (authority != null && !authority.isEmpty()) {
                        authorities.add(authority);
                    }
                }

                if (!authorities.isEmpty()) {
                    return authorities;
                }
            }

            throw new RuntimeException("No roles found in token");
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract roles from token", e);
        }
    }

    /**
     * Check if user has a specific role
     * @param token JWT token
     * @param requiredRole Role to check for
     * @return true if user has the required role, false otherwise
     */
    public boolean hasRole(String token, String requiredRole) {
        try {
            List<String> userRoles = extractRoles(token);

            // Loop through all user roles to check if any match the required role
            for (String userRole : userRoles) {
                if (userRole.equalsIgnoreCase(requiredRole)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if user has any of the required roles
     * @param token JWT token
     * @param requiredRoles List of roles to check for
     * @return true if user has any of the required roles, false otherwise
     */
    public boolean hasAnyRole(String token, List<String> requiredRoles) {
        try {
            List<String> userRoles = extractRoles(token);

            // Loop through all user roles
            for (String userRole : userRoles) {
                // Loop through required roles
                for (String requiredRole : requiredRoles) {
                    if (userRole.equalsIgnoreCase(requiredRole)) {
                        return true;
                    }
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract specific claim from token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}