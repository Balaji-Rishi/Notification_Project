package com.example.notificationapi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;

public class GenerateJwt {
    public static void main(String[] args) {
        String secret = "veryveryveryveryveryveryveryvery"; // must match application.yml
        long oneHour = 3600_000L;

        String token = Jwts.builder()
                .setSubject("postman-user")
                .setIssuer("notification-api")                // IMPORTANT: must match JwtUtils.requireIssuer
                .setClaims(Map.of("scope", "notifications.write"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + oneHour))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("\n=== JWT Token ===\n" + token + "\n");
    }
}
