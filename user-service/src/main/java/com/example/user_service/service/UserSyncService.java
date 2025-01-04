package com.example.user_service.service;

import com.example.user_service.dto.UserSyncRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserSyncService {
    private final RestTemplate restTemplate;

    public void syncUserWithDeviceService(Set<UUID> userIds){
        String deviceServiceUrl = "http://localhost:8082/api/device/sync";
        UserSyncRequest request = new UserSyncRequest(userIds);

        restTemplate.postForObject(deviceServiceUrl, request, String.class);
    }
}
