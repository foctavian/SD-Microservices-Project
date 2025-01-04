package com.example.monitoring_service.controller;

import com.example.monitoring_service.dto.MeasurementDTO;
import com.example.monitoring_service.service.MeasurementService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/monitoring")
@CrossOrigin("http://localhost:3000")
public class MonitoringController {
    private final MeasurementService measurementService;

   @GetMapping("/{userId}")
    public List<MeasurementDTO> getMeasurementsForUser(@PathVariable UUID userId){
        return measurementService.getMeasurementForUser(userId);
   }
}
