package com.example.monitoring_service.consumer;

import com.example.monitoring_service.dto.DeviceAction;
import com.example.monitoring_service.model.Device;
import com.example.monitoring_service.repository.DeviceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeviceSyncConsumer {
    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "deviceSyncQueue")
    public void syncDevice(String msg){
            try {
                DeviceAction entity = objectMapper.readValue(msg, DeviceAction.class);
                Device device = Device.builder().
                        assignedUser(UUID.fromString(entity.getAssignedUser())).
                        id(entity.getId()).
                        maxConsumption(entity.getMaxConsumption()).build();
                String action = entity.getAction();

                switch (action){
                    case "CREATE":
                        deviceRepository.save(device);
                        break;
                    case "DELETE":
                        deviceRepository.delete(device);
                        break;
                }
            } catch (JsonProcessingException e) {
                System.err.println("Failed to deserialize message: " + e.getMessage());
            }
    }
}
