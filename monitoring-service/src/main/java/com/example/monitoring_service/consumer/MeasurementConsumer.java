package com.example.monitoring_service.consumer;

import com.example.monitoring_service.controller.NotificationController;
import com.example.monitoring_service.dto.MeasurementDTO;
import com.example.monitoring_service.model.Measurement;
import com.example.monitoring_service.repository.MeasurementRepository;
import com.example.monitoring_service.service.DeviceService;
import com.example.monitoring_service.service.MeasurementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import static com.example.monitoring_service.service.MeasurementService.readMeasurementsDeviceBound;
@Component
@RequiredArgsConstructor
public class MeasurementConsumer {
    private final MeasurementService measurementService;
    private final DeviceService deviceService;
   private final SimpMessagingTemplate notificationTemplate;
    private final NotificationController notificationController;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<MeasurementDTO> measurementList = new ArrayList<>();
    @RabbitListener(queues = "measurementQueue")
    public void receiveMeasurement(byte[] byteMessage) {
        String message = new String(byteMessage, StandardCharsets.UTF_8);
        try {
            MeasurementDTO dto = objectMapper.readValue(message.replace("\\", ""), MeasurementDTO.class);
//
//            if(!readMeasurementsDeviceBound.containsKey(dto.getDevice())){
//                List<MeasurementDTO> readings = new ArrayList<>();
//                readings.add(dto);
//                readMeasurementsDeviceBound.put(dto.getDevice(), readings);
//            }
//            else{
//                readMeasurementsDeviceBound.get(dto.getDevice()).add(dto);
//            }
//            measurementService.updateMeasurements(dto.getDevice());
//            UUID user = deviceService.getUserForDevice(dto.getDevice());
//            String notificationMessage = "New measurement received for device: " + dto.getDevice();
//            notificationTemplate.convertAndSend("/topic/notification", Map.of(
//                    "title", "Measurement Update",
//                    "message", notificationMessage
//            ));
//            if(measurementService.maxConsumptionExceeded(dto.getDevice())){
//                notificationController.sendNotification( "Max consumption exceeded");
//            }
            measurementList.add(dto);

            //need to save in the db as well
            measurementService.saveMeasurement(dto);
            if(measurementList.size() == 6){
                MeasurementDTO first = measurementList.get(0);
                MeasurementDTO last = measurementList.get(5);

                double diff = last.getHourlyReading() - first.getHourlyReading();

                UUID deviceId = dto.getDevice();

                deviceService.findByDeviceId(deviceId).ifPresentOrElse(measurement -> {
                   if(diff > measurement.getMaxConsumption()){
                       String notification = "Max consumption threshold exceeded!";
                       notificationController.sendWarning(notification,measurement.getAssignedUser());
                   }
                },()->{});

                measurementList.clear();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
