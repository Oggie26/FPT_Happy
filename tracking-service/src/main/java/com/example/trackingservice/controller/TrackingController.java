package com.example.trackingservice.controller;

import com.example.trackingservice.dto.LocationResponse;
import com.example.trackingservice.dto.LocationUpdateRequest;
import com.example.trackingservice.dto.OrderTrackingResponse;
import com.example.trackingservice.model.DeliveryStatus;
import com.example.trackingservice.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking Controller", description = "GPS tracking and order tracking APIs")
public class TrackingController {

    private final TrackingService trackingService;
    private final SimpMessagingTemplate messagingTemplate;

    // Location tracking endpoints
    @PostMapping("/location")
    @Operation(summary = "Update driver location", description = "Update GPS location for a driver")
    public ResponseEntity<LocationResponse> updateLocation(@Valid @RequestBody LocationUpdateRequest request) {
        LocationResponse response = trackingService.updateLocation(request);
        
        // Send real-time update via WebSocket
        messagingTemplate.convertAndSend("/topic/location/" + request.getDriverId(), response);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/location/{driverId}")
    @Operation(summary = "Get current driver location", description = "Get the current GPS location of a driver")
    public ResponseEntity<LocationResponse> getCurrentLocation(@PathVariable String driverId) {
        LocationResponse location = trackingService.getCurrentLocation(driverId);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/location/{driverId}/history")
    @Operation(summary = "Get location history", description = "Get location history for a driver")
    public ResponseEntity<List<LocationResponse>> getLocationHistory(
            @PathVariable String driverId,
            @RequestParam(required = false) String orderId) {
        List<LocationResponse> history = trackingService.getLocationHistory(driverId, orderId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/location/active")
    @Operation(summary = "Get active driver locations", description = "Get current locations of all active drivers")
    public ResponseEntity<List<LocationResponse>> getActiveDriverLocations() {
        List<LocationResponse> locations = trackingService.getActiveDriverLocations();
        return ResponseEntity.ok(locations);
    }

    // Order tracking endpoints
    @PostMapping("/order")
    @Operation(summary = "Create order tracking", description = "Create a new order tracking entry")
    public ResponseEntity<OrderTrackingResponse> createOrderTracking(
            @RequestParam String orderId,
            @RequestParam String driverId,
            @RequestParam String customerId,
            @RequestParam Double pickupLat,
            @RequestParam Double pickupLng,
            @RequestParam Double deliveryLat,
            @RequestParam Double deliveryLng) {
        
        OrderTrackingResponse response = trackingService.createOrderTracking(
                orderId, driverId, customerId, pickupLat, pickupLng, deliveryLat, deliveryLng);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/order/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the delivery status of an order")
    public ResponseEntity<OrderTrackingResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam DeliveryStatus status) {
        
        OrderTrackingResponse response = trackingService.updateOrderStatus(orderId, status);
        
        // Send real-time update via WebSocket
        messagingTemplate.convertAndSend("/topic/order/" + orderId, response);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/order/{orderId}/location")
    @Operation(summary = "Update order location", description = "Update the current location of an order")
    public ResponseEntity<OrderTrackingResponse> updateOrderLocation(
            @PathVariable String orderId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        OrderTrackingResponse response = trackingService.updateOrderLocation(orderId, latitude, longitude);
        
        // Send real-time update via WebSocket
        messagingTemplate.convertAndSend("/topic/order/" + orderId, response);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order tracking", description = "Get tracking information for a specific order")
    public ResponseEntity<OrderTrackingResponse> getOrderTracking(@PathVariable String orderId) {
        OrderTrackingResponse tracking = trackingService.getOrderTracking(orderId);
        if (tracking != null) {
            return ResponseEntity.ok(tracking);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/driver/{driverId}")
    @Operation(summary = "Get driver orders", description = "Get all orders for a specific driver")
    public ResponseEntity<List<OrderTrackingResponse>> getDriverOrders(@PathVariable String driverId) {
        List<OrderTrackingResponse> orders = trackingService.getDriverOrders(driverId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/order/customer/{customerId}")
    @Operation(summary = "Get customer orders", description = "Get all orders for a specific customer")
    public ResponseEntity<List<OrderTrackingResponse>> getCustomerOrders(@PathVariable String customerId) {
        List<OrderTrackingResponse> orders = trackingService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    // Simulation endpoints
    @PostMapping("/simulation/start")
    @Operation(summary = "Start location simulation", description = "Start GPS simulation for a driver")
    public ResponseEntity<String> startLocationSimulation(
            @RequestParam String driverId,
            @RequestParam String orderId) {
        trackingService.startLocationSimulation(driverId, orderId);
        return ResponseEntity.ok("Location simulation started for driver: " + driverId);
    }

    @PostMapping("/simulation/stop")
    @Operation(summary = "Stop location simulation", description = "Stop GPS simulation for a driver")
    public ResponseEntity<String> stopLocationSimulation(@RequestParam String driverId) {
        trackingService.stopLocationSimulation(driverId);
        return ResponseEntity.ok("Location simulation stopped for driver: " + driverId);
    }

    // Cache management endpoints
    @DeleteMapping("/cache/driver/{driverId}")
    @Operation(summary = "Clear driver cache", description = "Clear cached location for a driver")
    public ResponseEntity<String> clearDriverCache(@PathVariable String driverId) {
        trackingService.clearDriverLocationCache(driverId);
        return ResponseEntity.ok("Cache cleared for driver: " + driverId);
    }
}