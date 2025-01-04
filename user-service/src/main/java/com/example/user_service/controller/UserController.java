package com.example.user_service.controller;

import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.dto.UserSyncRequest;
import com.example.user_service.dto.UserVerificationDto;
import com.example.user_service.service.UserService;
import com.example.user_service.service.UserSyncService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String deviceSyncUrl ="http://device-service:8082/api/device/sync";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest userRequest){
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);

        UUID id = userService.createUser(userRequest);
        Set<UUID> ids = new HashSet<UUID>();
        ids.add(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UserSyncRequest userSyncRequest = new UserSyncRequest();
        userSyncRequest.setUserIds(ids);
        HttpEntity<UserSyncRequest> httpEntity = new HttpEntity<>(userSyncRequest, httpHeaders);
        restTemplate.exchange(deviceSyncUrl,
                HttpMethod.POST,
                httpEntity,
                UserSyncRequest.class);
    }

    @GetMapping
    public UserSyncRequest getAllUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id ){
        try{
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            Set<UUID> ids = new HashSet<UUID>();
            ids.add(id);
            UserSyncRequest userSyncRequest = new UserSyncRequest();
            userSyncRequest.setUserIds(ids);
            HttpEntity<UserSyncRequest> httpEntity = new HttpEntity<>(userSyncRequest, httpHeaders);

            restTemplate.exchange(deviceSyncUrl,
                    HttpMethod.DELETE,
                    httpEntity,
                    UserSyncRequest.class);

            userService.deleteById(id);

            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
