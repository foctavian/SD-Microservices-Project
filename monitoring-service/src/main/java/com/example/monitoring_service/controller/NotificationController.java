package com.example.monitoring_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendWarning(String message, UUID userId) {
        System.out.println("\n\n\n\n\n/topic/warnings/" + userId.toString() + "/"+ message); // Debug log
        messagingTemplate.convertAndSend("/topic/warnings/" + userId + "/", message);
    }


}
