package com.example.device_service.controller;

import com.example.device_service.dto.DeviceAssignation;
import com.example.device_service.dto.DeviceDto;
import com.example.device_service.dto.DeviceResponse;
import com.example.device_service.dto.UserSyncRequest;
import com.example.device_service.model.Device;
import com.example.device_service.service.DeviceService;
import com.example.device_service.service.UserService;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:3000")
public class DeviceController {
    private final DeviceService deviceService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createDevice(@RequestBody DeviceDto deviceRequest){
        deviceService.createDevice(deviceRequest);
    }

    @GetMapping
    public List<DeviceResponse> getAllDevices(){
        return deviceService.getAllDevices();
    }

    @PostMapping("/sync")
    public void syncUserIdsOnInsert(@RequestBody UserSyncRequest request){
            userService.syncUsers(request, false);
    }

    @DeleteMapping("/sync")
    public void syncUserIdsOnDelete(@RequestBody UserSyncRequest request){
        // firstly, delete the connection between a user and all his devices
        deviceService.deallocateDevice(request);
        userService.syncUsers(request, true);
    }

    @GetMapping("/get-devices/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public List<DeviceResponse> getDevices(@PathVariable UUID userId){
        return deviceService.getDevicesForUser(userId);
    }

    @PostMapping("/allocate-devices")
    @PreAuthorize("hasRole('ADMIN')")
    public void allocateDevices(@RequestBody DeviceAssignation deviceAssignation){

        deviceService.allocateDevice(userService.getUserById(deviceAssignation.getUserId()),
                deviceAssignation.getDeviceId());
    }
    
    @DeleteMapping("/{deviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDevice(@PathVariable UUID deviceId){
        deviceService.deleteDevice(deviceId);
    }

}
