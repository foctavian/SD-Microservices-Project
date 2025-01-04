package com.example.chat_service.service;

import com.example.chat_service.dto.TypingDto;
import com.example.chat_service.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatService {
    private final SimpMessagingTemplate simpMessageTemplate;

    public void sendMessage(ChatMessage message, UUID userId) {
        simpMessageTemplate.convertAndSend("/topic/user/" + userId, message);
    }

    public void broadcastToAllAdmins(ChatMessage msg){
        simpMessageTemplate.convertAndSend("/topic/admins", msg);

    }

    public void notifyUserAdminAssigned(UUID userId, UUID adminId){
        String notif = MessageFormat.format("The admin {0} has been assigned", adminId);
        simpMessageTemplate.convertAndSend("/topic/user/"+userId+"/notifications","{\"message\":\"" + notif + "\"}");
    }

    public void notifyUserWaitAdmin(UUID userId){
        String notif = "No admin available. Please wait!";
        simpMessageTemplate.convertAndSend("/topic/user/"+ userId+"/notifications",  "{\"message\":\"" + notif + "\"}" );
    }

    public void sendTypingNotification(TypingDto msg) {
        simpMessageTemplate.convertAndSend("/topic/typing", msg);
    }
}
