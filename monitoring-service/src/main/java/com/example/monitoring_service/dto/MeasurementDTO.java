package com.example.monitoring_service.dto;

import com.example.monitoring_service.model.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementDTO {
    private UUID device;
    private Double hourlyReading;
    private Timestamp timestamp;
}
