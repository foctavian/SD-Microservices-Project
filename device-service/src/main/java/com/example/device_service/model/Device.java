package com.example.device_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="device")
public class Device {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    private String description;
    private String address;
    private Double max_consumption;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
