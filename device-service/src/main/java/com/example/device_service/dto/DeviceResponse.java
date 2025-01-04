package com.example.device_service.dto;

import com.example.device_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponse {
    private UUID id;
    private String description;
    private String address;
    private double max_consumption;
    private User user;
}
