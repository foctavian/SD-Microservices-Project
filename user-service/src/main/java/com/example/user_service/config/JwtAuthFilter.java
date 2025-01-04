package com.example.user_service.config;

import com.example.user_service.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Getter
    private final UserService userDetailsService;

    private final Logger logger = Logger.getLogger(JwtAuthFilter.class.getName());

    public JwtAuthFilter(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            String token = null;
            String username = null;
            String role = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = JwtHelper.extractUsername(token);
                role = JwtHelper.extractRole(token);
            }

            logger.info("Role: " + role);


            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            logger.info("Current Authentication PRE: " + SecurityContextHolder.getContext().getAuthentication());
            logger.info("Username: " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the token
                if (JwtHelper.validateToken(token, userDetails)) {

                    // Here, you should ensure that 'role' is in the format "ROLE_<ROLE_NAME>"
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                    logger.info("User: " + username + " has been authenticated");

                    // Create the authentication token
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    logger.info("Current Authentication: " + SecurityContextHolder.getContext().getAuthentication());

                }
            }

            filterChain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}