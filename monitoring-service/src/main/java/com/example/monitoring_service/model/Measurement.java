package com.example.monitoring_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
    private Double hourlyReading;
    private Timestamp timestamp;
}
