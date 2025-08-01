package com.example.driverservice.dto;

import com.example.driverservice.model.DriverStatus;
import com.example.driverservice.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String driverId;
    private String userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String licenseNumber;
    private VehicleType vehicleType;
    private String vehicleNumber;
    private DriverStatus status;
    private Double rating;
    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private Double averageDeliveryTime;
    private Double currentLatitude;
    private Double currentLongitude;
    private Boolean isAvailable;
    private LocalDateTime lastActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}