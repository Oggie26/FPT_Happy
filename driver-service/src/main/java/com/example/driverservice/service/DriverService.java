package com.example.driverservice.service;

import com.example.driverservice.dto.DriverPerformanceResponse;
import com.example.driverservice.dto.DriverRequest;
import com.example.driverservice.dto.DriverResponse;
import com.example.driverservice.model.DriverStatus;
import com.example.driverservice.model.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public interface DriverService {
    
    // Driver management
    DriverResponse createDriver(DriverRequest request);
    DriverResponse updateDriver(String driverId, DriverRequest request);
    DriverResponse getDriver(String driverId);
    List<DriverResponse> getAllDrivers();
    void deleteDriver(String driverId);
    
    // Driver availability
    List<DriverResponse> getAvailableDrivers();
    DriverResponse updateDriverAvailability(String driverId, Boolean isAvailable);
    DriverResponse updateDriverLocation(String driverId, Double latitude, Double longitude);
    DriverResponse updateDriverStatus(String driverId, DriverStatus status);
    
    // Driver assignment
    DriverResponse assignDriverToOrder(String driverId, String orderId);
    DriverResponse unassignDriverFromOrder(String driverId, String orderId);
    List<DriverResponse> findDriversNearLocation(Double latitude, Double longitude, Double radius);
    
    // Performance tracking
    DriverPerformanceResponse createPerformanceRecord(String driverId, LocalDateTime periodStart, LocalDateTime periodEnd);
    List<DriverPerformanceResponse> getDriverPerformance(String driverId);
    DriverPerformanceResponse getDriverPerformanceByPeriod(String driverId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Statistics
    Double getDriverRating(String driverId);
    Integer getDriverTotalDeliveries(String driverId);
    Double getDriverTotalEarnings(String driverId);
    List<DriverResponse> getTopPerformers(Integer limit);
    
    // Vehicle management
    List<DriverResponse> getDriversByVehicleType(VehicleType vehicleType);
    DriverResponse updateVehicleInfo(String driverId, VehicleType vehicleType, String vehicleNumber);
}