package com.example.trackingservice.repository;

import com.example.trackingservice.model.DeliveryStatus;
import com.example.trackingservice.model.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, Long> {
    
    Optional<OrderTracking> findByOrderId(String orderId);
    
    List<OrderTracking> findByDriverId(String driverId);
    
    List<OrderTracking> findByCustomerId(String customerId);
    
    List<OrderTracking> findByStatus(DeliveryStatus status);
    
    List<OrderTracking> findByDriverIdAndStatus(String driverId, DeliveryStatus status);
    
    @Query("SELECT ot FROM OrderTracking ot WHERE ot.driverId = :driverId AND ot.status IN :statuses")
    List<OrderTracking> findByDriverIdAndStatusIn(@Param("driverId") String driverId, @Param("statuses") List<DeliveryStatus> statuses);
    
    @Query("SELECT ot FROM OrderTracking ot WHERE ot.customerId = :customerId AND ot.status IN :statuses")
    List<OrderTracking> findByCustomerIdAndStatusIn(@Param("customerId") String customerId, @Param("statuses") List<DeliveryStatus> statuses);
    
    boolean existsByOrderId(String orderId);
    
    void deleteByOrderId(String orderId);
}