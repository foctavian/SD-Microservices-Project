package com.example.chat_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingDto {
    private String sender;
    private String receiver;
    private Boolean typing;
}
