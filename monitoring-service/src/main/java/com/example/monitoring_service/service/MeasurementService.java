package com.example.monitoring_service.service;

import com.example.monitoring_service.controller.NotificationController;
import com.example.monitoring_service.dto.MeasurementDTO;
import com.example.monitoring_service.model.Device;
import com.example.monitoring_service.model.Measurement;
import com.example.monitoring_service.repository.DeviceRepository;
import com.example.monitoring_service.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final DeviceRepository deviceRepository;
    public static HashMap<UUID, List<MeasurementDTO>> readMeasurementsDeviceBound = new HashMap<>();

    //TODO first delete all the entries in the measurement table before deleting a device
    public void saveMeasurement(MeasurementDTO measurementDTO){
        Measurement measurement = Measurement.builder()
                .device(deviceRepository.getReferenceById(measurementDTO.getDevice()))
                .hourlyReading(measurementDTO.getHourlyReading())
                .timestamp(measurementDTO.getTimestamp())
                .build();
        measurementRepository.save(measurement);
    }

    public List<MeasurementDTO> getMeasurementForUser(UUID userId){
       //first fetch all the devices of the user
        List<UUID> userDevices = deviceRepository.findAll().stream()
                .filter(device -> {
                    return device.getAssignedUser() != null && device.getAssignedUser().equals(userId);
                })
                .map(Device::getId)
                .toList();

        return mapMeasurementToMeasurementDTO(measurementRepository.findAll().stream()
                .filter(measurement -> userDevices.contains(measurement.getDevice().getId()))
                .toList());
    }

    private List<MeasurementDTO> mapMeasurementToMeasurementDTO(List<Measurement> measurements) {
        return measurements.stream()
                .map(measurement -> new MeasurementDTO(
                        measurement.getDevice().getId(),
                        measurement.getHourlyReading(),
                        measurement.getTimestamp()
                ))
                .toList();
    }


}
