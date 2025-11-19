package com.example.notificationapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final String secret;
    private final String issuer;
    private final Key key;

    // Note the '@Value' annotation on constructor parameters (correct syntax)
    public JwtUtils(@Value("${security.jwt.secret}") String secret,
                    @Value("${security.jwt.issuer}") String issuer) {
        this.secret = secret;
        this.issuer = issuer;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
//                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
