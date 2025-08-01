package com.example.driverservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_performance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverPerformance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "driver_id", nullable = false)
    private String driverId;
    
    @Column(name = "period_start")
    private LocalDateTime periodStart;
    
    @Column(name = "period_end")
    private LocalDateTime periodEnd;
    
    @Column(name = "total_deliveries")
    private Integer totalDeliveries;
    
    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries;
    
    @Column(name = "failed_deliveries")
    private Integer failedDeliveries;
    
    @Column(name = "average_delivery_time")
    private Double averageDeliveryTime; // in minutes
    
    @Column(name = "total_distance")
    private Double totalDistance; // in kilometers
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "earnings")
    private Double earnings;
    
    @Column(name = "fuel_consumption")
    private Double fuelConsumption; // in liters
    
    @Column(name = "maintenance_hours")
    private Double maintenanceHours;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}