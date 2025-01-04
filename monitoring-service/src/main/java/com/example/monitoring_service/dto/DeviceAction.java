package com.example.monitoring_service.dto;

import com.example.monitoring_service.model.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAction {
    @JsonProperty("action")
    private String action;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("assignedUser")
    private String assignedUser;

    @JsonProperty("maxConsumption")
    private Double maxConsumption;
}
