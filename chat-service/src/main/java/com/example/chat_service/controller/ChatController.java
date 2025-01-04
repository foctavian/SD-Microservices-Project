package com.example.chat_service.controller;

import com.example.chat_service.dto.ConnectionDto;
import com.example.chat_service.dto.TypingDto;
import com.example.chat_service.model.ChatMessage;
import com.example.chat_service.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private static final UUID UNASSIGNED_ADMIN = new UUID(0L, 0L);
    private final ConcurrentHashMap<UUID, UUID> userToAdminMap = new ConcurrentHashMap<>();
    @MessageMapping("/user/chat")
    public ChatMessage handleUserMessage(@Payload String rawMsg){
        ChatMessage msg;
        logger.info("user msg received : {}", rawMsg);
        try{
            ObjectMapper mapper = new ObjectMapper();
            msg = mapper.readValue(rawMsg, ChatMessage.class);
            logger.info("Deserialized message : {}", msg);
            UUID userId = UUID.fromString(msg.getSenderId());
            if(userToAdminMap.containsKey(userId)){
                UUID assignedAdmin = userToAdminMap.get(userId);
                if(assignedAdmin == null){
                    chatService.notifyUserWaitAdmin(userId);
                }else {
                    chatService.sendMessage(msg, userId);
                }
            }
            else{
                chatService.broadcastToAllAdmins(msg);
                userToAdminMap.putIfAbsent(userId, UNASSIGNED_ADMIN);
                chatService.notifyUserWaitAdmin(userId);
            }
           return msg;
        }catch(Exception e){
            logger.error("Failed to deserialize" ,e);
        }
    return null;
   }

    @MessageMapping("/admin/chat")
    public ChatMessage handleAdminMessage(@Payload String rawMsg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ChatMessage msg = mapper.readValue(rawMsg, ChatMessage.class);
            logger.info("Deserialized message: {}", msg);

            UUID userId = UUID.fromString(msg.getReceiver()); // Admin's receiver is the user
            UUID adminId = UUID.fromString(msg.getSenderId());

            // Ensure the user-to-admin mapping is correct
            userToAdminMap.putIfAbsent(userId, adminId);

            // Forward the message to the user's topic
            chatService.sendMessage(msg, userId);

            return msg;
        } catch (Exception e) {
            logger.error("Failed to deserialize message", e);
            return null;
        }
    }


   @MessageMapping("admin/connect")
   public void adminConnect(String rawString){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ConnectionDto msg = mapper.readValue(rawString, ConnectionDto.class);
            logger.info("Deserialized connection string : {}", msg);

            userToAdminMap.putIfAbsent(msg.getUser(), msg.getAdmin());
            chatService.notifyUserAdminAssigned(msg.getUser(), msg.getAdmin());
        }catch(Exception e){
            logger.error("Failed to deserialize" ,e);
        }
   }

    @MessageMapping("admin/disconnect")
    public void adminDisconnect(String rawString){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ConnectionDto msg = mapper.readValue(rawString, ConnectionDto.class);
            logger.info("Deserialized disconnection string : {}", msg);

            userToAdminMap.remove(msg.getUser());
        }catch(Exception e){
            logger.error("Failed to deserialize" ,e);
        }
    }

    @MessageMapping("/typing")
    public void sendTypingNotification(@Payload String notification) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypingDto msg = mapper.readValue(notification, TypingDto.class);
            log.info("typing : "+msg.toString());
            if(msg.getReceiver().toString().equals("unassigned")){
                UUID assignedAdmin = userToAdminMap.get(UUID.fromString(msg.getSender()));
                msg.setReceiver(String.valueOf(assignedAdmin));
                log.info("admin : "+assignedAdmin.toString());
                chatService.sendTypingNotification(msg);
            }
            else{
                chatService.sendTypingNotification(msg);
            }
        }catch(Exception e){
            logger.error("Failed to deserialize" ,e);
        }
    }
}
