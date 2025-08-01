package com.example.trackingservice.service.impl;

import com.example.trackingservice.dto.LocationResponse;
import com.example.trackingservice.dto.LocationUpdateRequest;
import com.example.trackingservice.dto.OrderTrackingResponse;
import com.example.trackingservice.model.DeliveryStatus;
import com.example.trackingservice.model.Location;
import com.example.trackingservice.model.OrderTracking;
import com.example.trackingservice.model.TrackingStatus;
import com.example.trackingservice.repository.LocationRepository;
import com.example.trackingservice.repository.OrderTrackingRepository;
import com.example.trackingservice.service.TrackingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingServiceImpl implements TrackingService {
    
    private final LocationRepository locationRepository;
    private final OrderTrackingRepository orderTrackingRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${tracking.kafka.topics.location-updates}")
    private String locationUpdatesTopic;
    
    @Value("${tracking.kafka.topics.delivery-status}")
    private String deliveryStatusTopic;
    
    @Value("${tracking.redis.keys.driver-locations}")
    private String driverLocationsKey;
    
    @Value("${tracking.redis.keys.order-tracking}")
    private String orderTrackingKey;
    
    @Value("${tracking.gps.simulation-enabled:true}")
    private boolean simulationEnabled;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<String, ScheduledExecutorService> driverSimulations = new ConcurrentHashMap<>();
    
    @Override
    public LocationResponse updateLocation(LocationUpdateRequest request) {
        try {
            Location location = Location.builder()
                    .driverId(request.getDriverId())
                    .orderId(request.getOrderId())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .altitude(request.getAltitude())
                    .speed(request.getSpeed())
                    .heading(request.getHeading())
                    .accuracy(request.getAccuracy())
                    .timestamp(LocalDateTime.now())
                    .status(TrackingStatus.ACTIVE)
                    .build();
            
            Location savedLocation = locationRepository.save(location);
            LocationResponse response = mapToLocationResponse(savedLocation);
            
            // Cache the location
            cacheDriverLocation(request.getDriverId(), response);
            
            // Publish to Kafka
            publishLocationUpdate(response);
            
            return response;
        } catch (Exception e) {
            log.error("Error updating location for driver: {}", request.getDriverId(), e);
            throw new RuntimeException("Failed to update location", e);
        }
    }
    
    @Override
    public LocationResponse getCurrentLocation(String driverId) {
        // First try to get from cache
        LocationResponse cachedLocation = getCachedDriverLocation(driverId);
        if (cachedLocation != null) {
            return cachedLocation;
        }
        
        // If not in cache, get from database
        Optional<Location> location = locationRepository.findFirstByDriverIdOrderByTimestampDesc(driverId);
        if (location.isPresent()) {
            LocationResponse response = mapToLocationResponse(location.get());
            cacheDriverLocation(driverId, response);
            return response;
        }
        
        return null;
    }
    
    @Override
    public List<LocationResponse> getLocationHistory(String driverId, String orderId) {
        List<Location> locations;
        if (orderId != null) {
            locations = locationRepository.findByOrderIdOrderByTimestampDesc(orderId);
        } else {
            locations = locationRepository.findByDriverIdOrderByTimestampDesc(driverId);
        }
        
        return locations.stream()
                .map(this::mapToLocationResponse)
                .toList();
    }
    
    @Override
    public List<LocationResponse> getActiveDriverLocations() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        List<String> activeDriverIds = locationRepository.findActiveDrivers(startTime);
        
        return activeDriverIds.stream()
                .map(this::getCurrentLocation)
                .filter(Objects::nonNull)
                .toList();
    }
    
    @Override
    public OrderTrackingResponse createOrderTracking(String orderId, String driverId, String customerId, 
                                                  Double pickupLat, Double pickupLng, 
                                                  Double deliveryLat, Double deliveryLng) {
        OrderTracking orderTracking = OrderTracking.builder()
                .orderId(orderId)
                .driverId(driverId)
                .customerId(customerId)
                .pickupLatitude(pickupLat)
                .pickupLongitude(pickupLng)
                .deliveryLatitude(deliveryLat)
                .deliveryLongitude(deliveryLng)
                .status(DeliveryStatus.ASSIGNED)
                .build();
        
        OrderTracking savedTracking = orderTrackingRepository.save(orderTracking);
        return mapToOrderTrackingResponse(savedTracking);
    }
    
    @Override
    public OrderTrackingResponse updateOrderStatus(String orderId, DeliveryStatus status) {
        Optional<OrderTracking> optional = orderTrackingRepository.findByOrderId(orderId);
        if (optional.isPresent()) {
            OrderTracking tracking = optional.get();
            tracking.setStatus(status);
            tracking.setUpdatedAt(LocalDateTime.now());
            
            if (status == DeliveryStatus.DELIVERED) {
                tracking.setActualArrival(LocalDateTime.now());
            }
            
            OrderTracking saved = orderTrackingRepository.save(tracking);
            publishDeliveryStatusUpdate(mapToOrderTrackingResponse(saved));
            return mapToOrderTrackingResponse(saved);
        }
        throw new RuntimeException("Order tracking not found: " + orderId);
    }
    
    @Override
    public OrderTrackingResponse updateOrderLocation(String orderId, Double latitude, Double longitude) {
        Optional<OrderTracking> optional = orderTrackingRepository.findByOrderId(orderId);
        if (optional.isPresent()) {
            OrderTracking tracking = optional.get();
            tracking.setCurrentLatitude(latitude);
            tracking.setCurrentLongitude(longitude);
            tracking.setUpdatedAt(LocalDateTime.now());
            
            OrderTracking saved = orderTrackingRepository.save(tracking);
            return mapToOrderTrackingResponse(saved);
        }
        throw new RuntimeException("Order tracking not found: " + orderId);
    }
    
    @Override
    public OrderTrackingResponse getOrderTracking(String orderId) {
        Optional<OrderTracking> optional = orderTrackingRepository.findByOrderId(orderId);
        return optional.map(this::mapToOrderTrackingResponse).orElse(null);
    }
    
    @Override
    public List<OrderTrackingResponse> getDriverOrders(String driverId) {
        return orderTrackingRepository.findByDriverId(driverId).stream()
                .map(this::mapToOrderTrackingResponse)
                .toList();
    }
    
    @Override
    public List<OrderTrackingResponse> getCustomerOrders(String customerId) {
        return orderTrackingRepository.findByCustomerId(customerId).stream()
                .map(this::mapToOrderTrackingResponse)
                .toList();
    }
    
    @Override
    public void startLocationSimulation(String driverId, String orderId) {
        if (!simulationEnabled) {
            log.warn("GPS simulation is disabled");
            return;
        }
        
        stopLocationSimulation(driverId);
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        driverSimulations.put(driverId, executor);
        
        executor.scheduleAtFixedRate(() -> {
            try {
                // Generate random location near the delivery point
                LocationUpdateRequest request = generateSimulatedLocation(driverId, orderId);
                updateLocation(request);
            } catch (Exception e) {
                log.error("Error in location simulation for driver: {}", driverId, e);
            }
        }, 0, 5, TimeUnit.SECONDS);
        
        log.info("Started location simulation for driver: {}", driverId);
    }
    
    @Override
    public void stopLocationSimulation(String driverId) {
        ScheduledExecutorService executor = driverSimulations.remove(driverId);
        if (executor != null) {
            executor.shutdown();
            log.info("Stopped location simulation for driver: {}", driverId);
        }
    }
    
    @Override
    public void cacheDriverLocation(String driverId, LocationResponse location) {
        try {
            String key = driverLocationsKey + ":" + driverId;
            String value = objectMapper.writeValueAsString(location);
            redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.error("Error caching driver location", e);
        }
    }
    
    @Override
    public LocationResponse getCachedDriverLocation(String driverId) {
        try {
            String key = driverLocationsKey + ":" + driverId;
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return objectMapper.readValue(value, LocationResponse.class);
            }
        } catch (Exception e) {
            log.error("Error getting cached driver location", e);
        }
        return null;
    }
    
    @Override
    public void clearDriverLocationCache(String driverId) {
        String key = driverLocationsKey + ":" + driverId;
        redisTemplate.delete(key);
    }
    
    private LocationResponse mapToLocationResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .driverId(location.getDriverId())
                .orderId(location.getOrderId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .altitude(location.getAltitude())
                .speed(location.getSpeed())
                .heading(location.getHeading())
                .accuracy(location.getAccuracy())
                .timestamp(location.getTimestamp())
                .status(location.getStatus())
                .createdAt(location.getCreatedAt())
                .build();
    }
    
    private OrderTrackingResponse mapToOrderTrackingResponse(OrderTracking tracking) {
        return OrderTrackingResponse.builder()
                .id(tracking.getId())
                .orderId(tracking.getOrderId())
                .driverId(tracking.getDriverId())
                .customerId(tracking.getCustomerId())
                .pickupLatitude(tracking.getPickupLatitude())
                .pickupLongitude(tracking.getPickupLongitude())
                .deliveryLatitude(tracking.getDeliveryLatitude())
                .deliveryLongitude(tracking.getDeliveryLongitude())
                .currentLatitude(tracking.getCurrentLatitude())
                .currentLongitude(tracking.getCurrentLongitude())
                .estimatedArrival(tracking.getEstimatedArrival())
                .actualArrival(tracking.getActualArrival())
                .status(tracking.getStatus())
                .createdAt(tracking.getCreatedAt())
                .updatedAt(tracking.getUpdatedAt())
                .build();
    }
    
    private LocationUpdateRequest generateSimulatedLocation(String driverId, String orderId) {
        // Get order tracking to simulate movement towards delivery point
        Optional<OrderTracking> optional = orderTrackingRepository.findByOrderId(orderId);
        if (optional.isPresent()) {
            OrderTracking tracking = optional.get();
            
            // Simulate movement from current position towards delivery point
            double currentLat = tracking.getCurrentLatitude() != null ? tracking.getCurrentLatitude() : tracking.getPickupLatitude();
            double currentLng = tracking.getCurrentLongitude() != null ? tracking.getCurrentLongitude() : tracking.getPickupLongitude();
            
            double deliveryLat = tracking.getDeliveryLatitude();
            double deliveryLng = tracking.getDeliveryLongitude();
            
            double latDiff = deliveryLat - currentLat;
            double lngDiff = deliveryLng - currentLng;
            
            double stepSize = 0.0001;
            double randomFactor = 0.5 + Math.random() * 0.5;
            
            double newLat = currentLat + (latDiff * stepSize * randomFactor);
            double newLng = currentLng + (lngDiff * stepSize * randomFactor);
            
            return LocationUpdateRequest.builder()
                    .driverId(driverId)
                    .orderId(orderId)
                    .latitude(newLat)
                    .longitude(newLng)
                    .speed(30.0 + Math.random() * 20.0) // 30-50 km/h
                    .heading(Math.random() * 360.0)
                    .accuracy(5.0 + Math.random() * 10.0) // 5-15 meters
                    .build();
        }
        
        return LocationUpdateRequest.builder()
                .driverId(driverId)
                .orderId(orderId)
                .latitude(10.762622 + (Math.random() - 0.5) * 0.01)
                .longitude(106.660172 + (Math.random() - 0.5) * 0.01)
                .speed(30.0 + Math.random() * 20.0)
                .heading(Math.random() * 360.0)
                .accuracy(5.0 + Math.random() * 10.0)
                .build();
    }
    
    private void publishLocationUpdate(LocationResponse location) {
        try {
            String message = objectMapper.writeValueAsString(location);
            kafkaTemplate.send(locationUpdatesTopic, location.getDriverId(), message);
        } catch (JsonProcessingException e) {
            log.error("Error publishing location update", e);
        }
    }
    
    private void publishDeliveryStatusUpdate(OrderTrackingResponse tracking) {
        try {
            String message = objectMapper.writeValueAsString(tracking);
            kafkaTemplate.send(deliveryStatusTopic, tracking.getOrderId(), message);
        } catch (JsonProcessingException e) {
            log.error("Error publishing delivery status update", e);
        }
    }
}