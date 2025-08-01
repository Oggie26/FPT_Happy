package com.example.trackingservice.repository;

import com.example.trackingservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    List<Location> findByDriverIdOrderByTimestampDesc(String driverId);
    
    List<Location> findByOrderIdOrderByTimestampDesc(String orderId);
    
    Optional<Location> findFirstByDriverIdOrderByTimestampDesc(String driverId);
    
    @Query("SELECT l FROM Location l WHERE l.driverId = :driverId AND l.timestamp >= :startTime ORDER BY l.timestamp DESC")
    List<Location> findByDriverIdAndTimestampAfter(@Param("driverId") String driverId, @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT l FROM Location l WHERE l.orderId = :orderId AND l.timestamp >= :startTime ORDER BY l.timestamp DESC")
    List<Location> findByOrderIdAndTimestampAfter(@Param("orderId") String orderId, @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT DISTINCT l.driverId FROM Location l WHERE l.timestamp >= :startTime")
    List<String> findActiveDrivers(@Param("startTime") LocalDateTime startTime);
    
    void deleteByDriverId(String driverId);
    
    void deleteByOrderId(String orderId);
}