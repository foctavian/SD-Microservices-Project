package com.example.monitoring_service.service;

import com.example.monitoring_service.model.Device;
import com.example.monitoring_service.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public UUID getUserForDevice(UUID deviceId){
        return deviceRepository.findAll().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findFirst().get().getAssignedUser();
    }

    public Optional<Device> findByDeviceId(UUID id){
        return  deviceRepository.findAll().stream().filter(device-> device.getId().equals(id)).findFirst();
    }



}
