package com.example.device_service.service;

import com.example.device_service.dto.DeviceDto;
import com.example.device_service.dto.DeviceResponse;
import com.example.device_service.dto.UserSyncRequest;
import com.example.device_service.model.Device;
import com.example.device_service.model.User;
import com.example.device_service.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public void createDevice(DeviceDto deviceRequest) {
        Device device = Device.builder()
                .description(deviceRequest.getDescription())
                .address(deviceRequest.getAddress())
                .max_consumption(deviceRequest.getMax_consumption())
                .build();

        deviceRepository.save(device);
    }

    public List<DeviceResponse> getDevicesForUser(UUID userId){
        List<Device> devices= deviceRepository.findDevicesByUserId(userId);
        return devices.stream().map(this::mapToDeviceResponse).toList();
    }

    public List<DeviceResponse> getAllDevices(){
        List<Device> devices = deviceRepository.findAll();
        return devices.stream().map(this::mapToDeviceResponse).toList();
    }

    private DeviceResponse mapToDeviceResponse(Device device){
        return DeviceResponse.builder()
                .id(device.getId())
                .max_consumption(device.getMax_consumption())
                .description(device.getDescription())
                .address(device.getAddress())
                .user(device.getUser())
                .build();
    }



    public void allocateDevice(User user, UUID device) {
        Device deviceobj = deviceRepository.findById(device).get();
        deviceobj.setUser(user);
        Device savedDevice = deviceRepository.save(deviceobj);
        syncDevices(savedDevice, "CREATE");
    }

    public void deallocateDevice(UserSyncRequest request) {
        Set<UUID> userIds = request.getUserIds();
        for(UUID user:userIds) {
            List<Device> devices = deviceRepository.findDevicesByUserId(user);
            for(Device device:devices) {
                device.setUser(null);
                Device newDevice = deviceRepository.save(device);
                syncDevices(newDevice, "DELETE");
            }
        }
    }


    public void deleteDevice(UUID deviceId) {
        Device device = deviceRepository.getReferenceById(deviceId);
        syncDevices(device, "DELETE");
        deviceRepository.deleteById(deviceId);
    }

//    private void syncDevices(Device device){
//        Map<String, Object> sentDevice = new HashMap<>();
//
//        sentDevice.put("id", device.getId());
//        sentDevice.put("maxConsumption", device.getMax_consumption());
//        sentDevice.put("assignedUser", device.getUser().getId());
//
//        try{
//            String json = objectMapper.writeValueAsString(sentDevice);
//            rabbitTemplate.convertAndSend("exchange", "routing.device.sync", json);
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }
//    }

    private void syncDevices(Device device, String action){
        Map<String, Object> sentDevice = new HashMap<>();

        sentDevice.put("action", action);
        sentDevice.put("id", device.getId());
        sentDevice.put("maxConsumption", device.getMax_consumption());
        sentDevice.put("assignedUser", device.getUser().getId());

        try{
            String json = objectMapper.writeValueAsString(sentDevice);
            rabbitTemplate.convertAndSend("exchange", "routing.device.sync", json);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
