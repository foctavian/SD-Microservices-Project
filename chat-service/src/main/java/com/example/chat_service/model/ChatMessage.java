package com.example.chat_service.model;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private String senderId;
    private String receiver;
    private String message;
    private Timestamp timestamp;
}
