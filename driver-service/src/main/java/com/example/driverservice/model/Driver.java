package com.example.driverservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "driver_id", nullable = false, unique = true)
    private String driverId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "license_number", nullable = false)
    private String licenseNumber;
    
    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "total_deliveries")
    private Integer totalDeliveries;
    
    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries;
    
    @Column(name = "average_delivery_time")
    private Double averageDeliveryTime; // in minutes
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "is_available")
    private Boolean isAvailable;
    
    @Column(name = "last_active")
    private LocalDateTime lastActive;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DriverStatus.ACTIVE;
        }
        if (isAvailable == null) {
            isAvailable = true;
        }
        if (rating == null) {
            rating = 5.0;
        }
        if (totalDeliveries == null) {
            totalDeliveries = 0;
        }
        if (successfulDeliveries == null) {
            successfulDeliveries = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}