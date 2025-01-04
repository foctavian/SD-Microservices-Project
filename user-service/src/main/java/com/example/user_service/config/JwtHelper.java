package com.example.user_service.config;

import com.example.user_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JwtHelper {
    private static final String SECRET_KEY = "5hbml9xTQtykB+cF59XLIuFV9yHFhnQ7cqX9ba5fDPU=";
    private static final int MINUTES = 60;
    private static final Logger logger = Logger.getLogger(JwtHelper.class.getName());
    public static String generateToken(UserDetails user) {
        var now = Instant.now();

        Map<String, Object> claims = new HashMap<>();
        String role = user.getAuthorities().stream()
                .findFirst() // Get the first authority (role)
                .map(GrantedAuthority::getAuthority) // Extract the string representation
                .orElse(null);
        claims.put("role", role);
        claims.put("username", user.getUsername());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (Boolean) (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    private static boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public static int getExpiration(){
        return MINUTES;
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }



    public static String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

}
