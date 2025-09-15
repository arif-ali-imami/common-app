package com.echoItSolution.common_app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private SecretKey getSecretKey(){
        String secretKey = "gnr4ng458hjtg80459gh458g4580jg45rg";
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /** Extract a custom claim by name and type, e.g., ("userId", String.class). */
    public <T> T extractClaim(String token, String claimName, Class<T> type) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName, type);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token); // will throw if invalid or expired
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        // Will throw if token is tampered/invalid/expired (ExpiredJwtException extends JwtException)
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("sub", String.class);
    }

    public Set<String> getRoles1(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof Collection<?>) {
            // Convert collection elements to String and return as Set
            return ((Collection<?>) rolesObject).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
    public Set<String> getRoles(String token) {
        Claims claims = extractAllClaims(token);

        // "roles" is an array of objects like { "authority": "ROLE_ADMIN" }
        List<Map<String, String>> roles = claims.get("roles", List.class);

        if (roles == null) {
            return Set.of();
        }

        return roles.stream()
                .map(roleMap -> roleMap.get("authority")) // only take "authority" field
                .collect(Collectors.toSet());
    }
}
