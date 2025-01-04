package com.example.monitoring_service.repository;

import com.example.monitoring_service.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {
    
}
