package com.example.chat_service.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class ConnectionDto {
    UUID user;
    UUID admin;
}
