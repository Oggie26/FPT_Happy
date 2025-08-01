package com.example.trackingservice.service;

import com.example.trackingservice.dto.LocationResponse;
import com.example.trackingservice.dto.LocationUpdateRequest;
import com.example.trackingservice.dto.OrderTrackingResponse;
import com.example.trackingservice.model.DeliveryStatus;

import java.util.List;

public interface TrackingService {
    
    // Location tracking
    LocationResponse updateLocation(LocationUpdateRequest request);
    LocationResponse getCurrentLocation(String driverId);
    List<LocationResponse> getLocationHistory(String driverId, String orderId);
    List<LocationResponse> getActiveDriverLocations();
    
    // Order tracking
    OrderTrackingResponse createOrderTracking(String orderId, String driverId, String customerId, 
                                           Double pickupLat, Double pickupLng, 
                                           Double deliveryLat, Double deliveryLng);
    OrderTrackingResponse updateOrderStatus(String orderId, DeliveryStatus status);
    OrderTrackingResponse updateOrderLocation(String orderId, Double latitude, Double longitude);
    OrderTrackingResponse getOrderTracking(String orderId);
    List<OrderTrackingResponse> getDriverOrders(String driverId);
    List<OrderTrackingResponse> getCustomerOrders(String customerId);
    
    // Simulation
    void startLocationSimulation(String driverId, String orderId);
    void stopLocationSimulation(String driverId);
    
    // Cache management
    void cacheDriverLocation(String driverId, LocationResponse location);
    LocationResponse getCachedDriverLocation(String driverId);
    void clearDriverLocationCache(String driverId);
}