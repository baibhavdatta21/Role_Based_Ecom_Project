package com.ecommerce.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.management.relation.Role;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String secretKey =
            "Iax5+gIYQsITpUJLZLLMMKeAhnDzo6S6y9DUnlld6F4kB89cG80ZlJpX3vXTSO8a9vvzvwkN3C+8SZryP17ESQ==";
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }


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


    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()
                        + 1000L * 60 * 60 * 24))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token,
                               Function<Claims, T> claimResolver) {

        final Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}