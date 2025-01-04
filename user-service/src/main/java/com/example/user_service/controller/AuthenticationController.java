package com.example.user_service.controller;


import com.example.user_service.config.JwtHelper;
import com.example.user_service.dto.LoginUserDto;

import com.example.user_service.dto.UserVerificationDto;
import com.example.user_service.service.UserService;
import io.jsonwebtoken.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final UserService userDetailsService;

    public AuthenticationController(AuthenticationManager authenticationManager, UserService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto input) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));
        } catch (BadCredentialsException e) {
           throw e;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(input.getUsername());
        String token = JwtHelper.generateToken(userDetails);
        UUID id = userDetailsService.getIdByUsername(input.getUsername());
        return ResponseEntity.ok(new UserVerificationDto(id, input.getUsername(),JwtHelper.extractRole(token), token, JwtHelper.getExpiration()));
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Boolean>> verify(@RequestBody String token) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            boolean isValid = JwtHelper.validateToken(token, userDetailsService.loadUserByUsername(JwtHelper.extractUsername(token)));
            response.put("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("valid", false);
            return ResponseEntity.ok(response);
        }
    }


}


