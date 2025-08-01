package com.example.driverservice.service.impl;

import com.example.driverservice.dto.DriverPerformanceResponse;
import com.example.driverservice.dto.DriverRequest;
import com.example.driverservice.dto.DriverResponse;
import com.example.driverservice.model.Driver;
import com.example.driverservice.model.DriverPerformance;
import com.example.driverservice.model.DriverStatus;
import com.example.driverservice.model.VehicleType;
import com.example.driverservice.repository.DriverPerformanceRepository;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    
    private final DriverRepository driverRepository;
    private final DriverPerformanceRepository performanceRepository;
    
    @Override
    public DriverResponse createDriver(DriverRequest request) {
        // Check if driver already exists
        if (driverRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("Driver already exists for user: " + request.getUserId());
        }
        
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists: " + request.getLicenseNumber());
        }
        
        Driver driver = Driver.builder()
                .driverId(generateDriverId())
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .licenseNumber(request.getLicenseNumber())
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber())
                .status(request.getStatus() != null ? request.getStatus() : DriverStatus.ACTIVE)
                .isAvailable(true)
                .rating(5.0)
                .totalDeliveries(0)
                .successfulDeliveries(0)
                .build();
        
        Driver savedDriver = driverRepository.save(driver);
        return mapToDriverResponse(savedDriver);
    }
    
    @Override
    public DriverResponse updateDriver(String driverId, DriverRequest request) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            
            driver.setFirstName(request.getFirstName());
            driver.setLastName(request.getLastName());
            driver.setPhoneNumber(request.getPhoneNumber());
            driver.setEmail(request.getEmail());
            driver.setLicenseNumber(request.getLicenseNumber());
            driver.setVehicleType(request.getVehicleType());
            driver.setVehicleNumber(request.getVehicleNumber());
            if (request.getStatus() != null) {
                driver.setStatus(request.getStatus());
            }
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public DriverResponse getDriver(String driverId) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        return optional.map(this::mapToDriverResponse).orElse(null);
    }
    
    @Override
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::mapToDriverResponse)
                .toList();
    }
    
    @Override
    public void deleteDriver(String driverId) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            driverRepository.delete(optional.get());
        } else {
            throw new RuntimeException("Driver not found: " + driverId);
        }
    }
    
    @Override
    public List<DriverResponse> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers().stream()
                .map(this::mapToDriverResponse)
                .toList();
    }
    
    @Override
    public DriverResponse updateDriverAvailability(String driverId, Boolean isAvailable) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setIsAvailable(isAvailable);
            driver.setLastActive(LocalDateTime.now());
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public DriverResponse updateDriverLocation(String driverId, Double latitude, Double longitude) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setCurrentLatitude(latitude);
            driver.setCurrentLongitude(longitude);
            driver.setLastActive(LocalDateTime.now());
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public DriverResponse updateDriverStatus(String driverId, DriverStatus status) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setStatus(status);
            
            if (status == DriverStatus.ACTIVE) {
                driver.setIsAvailable(true);
            } else if (status == DriverStatus.INACTIVE || status == DriverStatus.SUSPENDED) {
                driver.setIsAvailable(false);
            }
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public DriverResponse assignDriverToOrder(String driverId, String orderId) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setStatus(DriverStatus.ON_DELIVERY);
            driver.setIsAvailable(false);
            driver.setLastActive(LocalDateTime.now());
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public DriverResponse unassignDriverFromOrder(String driverId, String orderId) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setStatus(DriverStatus.ACTIVE);
            driver.setIsAvailable(true);
            driver.setLastActive(LocalDateTime.now());
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    @Override
    public List<DriverResponse> findDriversNearLocation(Double latitude, Double longitude, Double radius) {
        // Convert radius from km to degrees (approximate)
        double radiusInDegrees = radius / 111.0; // 1 degree ≈ 111 km
        
        double minLat = latitude - radiusInDegrees;
        double maxLat = latitude + radiusInDegrees;
        double minLng = longitude - radiusInDegrees;
        double maxLng = longitude + radiusInDegrees;
        
        return driverRepository.findDriversInArea(minLat, maxLat, minLng, maxLng).stream()
                .map(this::mapToDriverResponse)
                .toList();
    }
    
    @Override
    public DriverPerformanceResponse createPerformanceRecord(String driverId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        DriverPerformance performance = DriverPerformance.builder()
                .driverId(driverId)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .totalDeliveries(0)
                .successfulDeliveries(0)
                .failedDeliveries(0)
                .averageDeliveryTime(0.0)
                .totalDistance(0.0)
                .averageRating(5.0)
                .earnings(0.0)
                .fuelConsumption(0.0)
                .maintenanceHours(0.0)
                .build();
        
        DriverPerformance savedPerformance = performanceRepository.save(performance);
        return mapToDriverPerformanceResponse(savedPerformance);
    }
    
    @Override
    public List<DriverPerformanceResponse> getDriverPerformance(String driverId) {
        return performanceRepository.findByDriverIdOrderByPeriodStartDesc(driverId).stream()
                .map(this::mapToDriverPerformanceResponse)
                .toList();
    }
    
    @Override
    public DriverPerformanceResponse getDriverPerformanceByPeriod(String driverId, LocalDateTime startDate, LocalDateTime endDate) {
        List<DriverPerformance> performances = performanceRepository.findByDriverIdAndPeriodBetween(driverId, startDate, endDate);
        if (!performances.isEmpty()) {
            return mapToDriverPerformanceResponse(performances.get(0));
        }
        return null;
    }
    
    @Override
    public Double getDriverRating(String driverId) {
        return performanceRepository.findAverageRatingByDriverId(driverId).orElse(5.0);
    }
    
    @Override
    public Integer getDriverTotalDeliveries(String driverId) {
        return performanceRepository.findTotalDeliveriesByDriverId(driverId).orElse(0);
    }
    
    @Override
    public Double getDriverTotalEarnings(String driverId) {
        return performanceRepository.findTotalEarningsByDriverId(driverId).orElse(0.0);
    }
    
    @Override
    public List<DriverResponse> getTopPerformers(Integer limit) {
        return driverRepository.findAll().stream()
                .sorted((d1, d2) -> Double.compare(d2.getRating(), d1.getRating()))
                .limit(limit)
                .map(this::mapToDriverResponse)
                .toList();
    }
    
    @Override
    public List<DriverResponse> getDriversByVehicleType(VehicleType vehicleType) {
        return driverRepository.findByVehicleType(vehicleType).stream()
                .map(this::mapToDriverResponse)
                .toList();
    }
    
    @Override
    public DriverResponse updateVehicleInfo(String driverId, VehicleType vehicleType, String vehicleNumber) {
        Optional<Driver> optional = driverRepository.findByDriverId(driverId);
        if (optional.isPresent()) {
            Driver driver = optional.get();
            driver.setVehicleType(vehicleType);
            driver.setVehicleNumber(vehicleNumber);
            
            Driver savedDriver = driverRepository.save(driver);
            return mapToDriverResponse(savedDriver);
        }
        throw new RuntimeException("Driver not found: " + driverId);
    }
    
    private String generateDriverId() {
        return "DRV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private DriverResponse mapToDriverResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .driverId(driver.getDriverId())
                .userId(driver.getUserId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .phoneNumber(driver.getPhoneNumber())
                .email(driver.getEmail())
                .licenseNumber(driver.getLicenseNumber())
                .vehicleType(driver.getVehicleType())
                .vehicleNumber(driver.getVehicleNumber())
                .status(driver.getStatus())
                .rating(driver.getRating())
                .totalDeliveries(driver.getTotalDeliveries())
                .successfulDeliveries(driver.getSuccessfulDeliveries())
                .averageDeliveryTime(driver.getAverageDeliveryTime())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .isAvailable(driver.getIsAvailable())
                .lastActive(driver.getLastActive())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
    
    private DriverPerformanceResponse mapToDriverPerformanceResponse(DriverPerformance performance) {
        return DriverPerformanceResponse.builder()
                .id(performance.getId())
                .driverId(performance.getDriverId())
                .periodStart(performance.getPeriodStart())
                .periodEnd(performance.getPeriodEnd())
                .totalDeliveries(performance.getTotalDeliveries())
                .successfulDeliveries(performance.getSuccessfulDeliveries())
                .failedDeliveries(performance.getFailedDeliveries())
                .averageDeliveryTime(performance.getAverageDeliveryTime())
                .totalDistance(performance.getTotalDistance())
                .averageRating(performance.getAverageRating())
                .earnings(performance.getEarnings())
                .fuelConsumption(performance.getFuelConsumption())
                .maintenanceHours(performance.getMaintenanceHours())
                .createdAt(performance.getCreatedAt())
                .build();
    }
}