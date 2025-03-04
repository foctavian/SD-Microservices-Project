package com.example.device_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "5hbml9xTQtykB+cF59XLIuFV9yHFhnQ7cqX9ba5fDPU=";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }
        if(jwt!= null){
            try{
                byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
                Jws<Claims> claimsJws =Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(keyBytes)).build().parseClaimsJws(jwt);
                Claims claims = claimsJws.getBody();
                String role = claims.get("role", String.class);
                String username = claims.get("username", String.class);

                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(username, null, authorities)
                );
            }catch(Exception e){}
        }
        filterChain.doFilter(request, response);

    }
}
