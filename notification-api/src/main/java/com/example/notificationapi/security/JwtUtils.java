package com.example.notificationapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    // In real projects, move this to application.yml and use @Value
    private static final String SECRET_KEY = "change-this-secret-key-change-this-secret-key-123";
    private static final long EXPIRATION_MS = 60 * 60 * 1000; // 1 hour

    private final Key key;
    private final JwtParser parser;

    public JwtUtils() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    /**
     * Create JWT for authenticated user.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" ")); // e.g. "ROLE_USER ROLE_ADMIN"

        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(username)
                .claim("scope", scope)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token and return parsed claims.
     * Throws exceptions if invalid/expired.
     */
    public Jws<Claims> validate(String token) {
        return parser.parseClaimsJws(token);
    }
}
