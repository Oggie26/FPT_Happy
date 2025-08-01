package com.example.driverservice.controller;

import com.example.driverservice.dto.DriverPerformanceResponse;
import com.example.driverservice.dto.DriverRequest;
import com.example.driverservice.dto.DriverResponse;
import com.example.driverservice.model.DriverStatus;
import com.example.driverservice.model.VehicleType;
import com.example.driverservice.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver Controller", description = "Driver management and performance APIs")
public class DriverController {

    private final DriverService driverService;

    // Driver management endpoints
    @PostMapping
    @Operation(summary = "Create new driver", description = "Create a new driver profile")
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{driverId}")
    @Operation(summary = "Update driver", description = "Update driver information")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable String driverId, @Valid @RequestBody DriverRequest request) {
        DriverResponse response = driverService.updateDriver(driverId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Get driver", description = "Get driver information by ID")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable String driverId) {
        DriverResponse driver = driverService.getDriver(driverId);
        if (driver != null) {
            return ResponseEntity.ok(driver);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Get list of all drivers")
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        List<DriverResponse> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @DeleteMapping("/{driverId}")
    @Operation(summary = "Delete driver", description = "Delete a driver profile")
    public ResponseEntity<String> deleteDriver(@PathVariable String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.ok("Driver deleted successfully");
    }

    // Driver availability endpoints
    @GetMapping("/available")
    @Operation(summary = "Get available drivers", description = "Get list of available drivers")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
        List<DriverResponse> drivers = driverService.getAvailableDrivers();
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{driverId}/availability")
    @Operation(summary = "Update driver availability", description = "Update driver availability status")
    public ResponseEntity<DriverResponse> updateDriverAvailability(
            @PathVariable String driverId,
            @RequestParam Boolean isAvailable) {
        DriverResponse response = driverService.updateDriverAvailability(driverId, isAvailable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}/location")
    @Operation(summary = "Update driver location", description = "Update driver's current GPS location")
    public ResponseEntity<DriverResponse> updateDriverLocation(
            @PathVariable String driverId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        DriverResponse response = driverService.updateDriverLocation(driverId, latitude, longitude);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}/status")
    @Operation(summary = "Update driver status", description = "Update driver status")
    public ResponseEntity<DriverResponse> updateDriverStatus(
            @PathVariable String driverId,
            @RequestParam DriverStatus status) {
        DriverResponse response = driverService.updateDriverStatus(driverId, status);
        return ResponseEntity.ok(response);
    }

    // Driver assignment endpoints
    @PostMapping("/{driverId}/assign/{orderId}")
    @Operation(summary = "Assign driver to order", description = "Assign a driver to a specific order")
    public ResponseEntity<DriverResponse> assignDriverToOrder(
            @PathVariable String driverId,
            @PathVariable String orderId) {
        DriverResponse response = driverService.assignDriverToOrder(driverId, orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{driverId}/unassign/{orderId}")
    @Operation(summary = "Unassign driver from order", description = "Unassign a driver from a specific order")
    public ResponseEntity<DriverResponse> unassignDriverFromOrder(
            @PathVariable String driverId,
            @PathVariable String orderId) {
        DriverResponse response = driverService.unassignDriverFromOrder(driverId, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/near-location")
    @Operation(summary = "Find drivers near location", description = "Find drivers within a specified radius")
    public ResponseEntity<List<DriverResponse>> findDriversNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radius) {
        List<DriverResponse> drivers = driverService.findDriversNearLocation(latitude, longitude, radius);
        return ResponseEntity.ok(drivers);
    }

    // Performance tracking endpoints
    @PostMapping("/{driverId}/performance")
    @Operation(summary = "Create performance record", description = "Create a new performance record for a driver")
    public ResponseEntity<DriverPerformanceResponse> createPerformanceRecord(
            @PathVariable String driverId,
            @RequestParam LocalDateTime periodStart,
            @RequestParam LocalDateTime periodEnd) {
        DriverPerformanceResponse response = driverService.createPerformanceRecord(driverId, periodStart, periodEnd);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{driverId}/performance")
    @Operation(summary = "Get driver performance", description = "Get performance records for a driver")
    public ResponseEntity<List<DriverPerformanceResponse>> getDriverPerformance(@PathVariable String driverId) {
        List<DriverPerformanceResponse> performance = driverService.getDriverPerformance(driverId);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/{driverId}/performance/period")
    @Operation(summary = "Get driver performance by period", description = "Get driver performance for a specific period")
    public ResponseEntity<DriverPerformanceResponse> getDriverPerformanceByPeriod(
            @PathVariable String driverId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        DriverPerformanceResponse performance = driverService.getDriverPerformanceByPeriod(driverId, startDate, endDate);
        if (performance != null) {
            return ResponseEntity.ok(performance);
        }
        return ResponseEntity.notFound().build();
    }

    // Statistics endpoints
    @GetMapping("/{driverId}/rating")
    @Operation(summary = "Get driver rating", description = "Get average rating for a driver")
    public ResponseEntity<Double> getDriverRating(@PathVariable String driverId) {
        Double rating = driverService.getDriverRating(driverId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/{driverId}/deliveries")
    @Operation(summary = "Get driver total deliveries", description = "Get total number of deliveries for a driver")
    public ResponseEntity<Integer> getDriverTotalDeliveries(@PathVariable String driverId) {
        Integer deliveries = driverService.getDriverTotalDeliveries(driverId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/{driverId}/earnings")
    @Operation(summary = "Get driver total earnings", description = "Get total earnings for a driver")
    public ResponseEntity<Double> getDriverTotalEarnings(@PathVariable String driverId) {
        Double earnings = driverService.getDriverTotalEarnings(driverId);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/top-performers")
    @Operation(summary = "Get top performers", description = "Get list of top performing drivers")
    public ResponseEntity<List<DriverResponse>> getTopPerformers(@RequestParam(defaultValue = "10") Integer limit) {
        List<DriverResponse> performers = driverService.getTopPerformers(limit);
        return ResponseEntity.ok(performers);
    }

    // Vehicle management endpoints
    @GetMapping("/vehicle-type/{vehicleType}")
    @Operation(summary = "Get drivers by vehicle type", description = "Get all drivers with a specific vehicle type")
    public ResponseEntity<List<DriverResponse>> getDriversByVehicleType(@PathVariable VehicleType vehicleType) {
        List<DriverResponse> drivers = driverService.getDriversByVehicleType(vehicleType);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{driverId}/vehicle")
    @Operation(summary = "Update vehicle info", description = "Update vehicle information for a driver")
    public ResponseEntity<DriverResponse> updateVehicleInfo(
            @PathVariable String driverId,
            @RequestParam VehicleType vehicleType,
            @RequestParam String vehicleNumber) {
        DriverResponse response = driverService.updateVehicleInfo(driverId, vehicleType, vehicleNumber);
        return ResponseEntity.ok(response);
    }
}