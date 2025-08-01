package com.example.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverPerformanceResponse {
    private Long id;
    private String driverId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private Integer failedDeliveries;
    private Double averageDeliveryTime;
    private Double totalDistance;
    private Double averageRating;
    private Double earnings;
    private Double fuelConsumption;
    private Double maintenanceHours;
    private LocalDateTime createdAt;
}