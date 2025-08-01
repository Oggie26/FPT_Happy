package com.example.driverservice.repository;

import com.example.driverservice.model.DriverPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverPerformanceRepository extends JpaRepository<DriverPerformance, Long> {
    
    List<DriverPerformance> findByDriverId(String driverId);
    
    List<DriverPerformance> findByDriverIdOrderByPeriodStartDesc(String driverId);
    
    @Query("SELECT dp FROM DriverPerformance dp WHERE dp.driverId = :driverId AND dp.periodStart >= :startDate")
    List<DriverPerformance> findByDriverIdAndPeriodStartAfter(@Param("driverId") String driverId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT dp FROM DriverPerformance dp WHERE dp.periodStart BETWEEN :startDate AND :endDate")
    List<DriverPerformance> findByPeriodBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT dp FROM DriverPerformance dp WHERE dp.driverId = :driverId AND dp.periodStart BETWEEN :startDate AND :endDate")
    List<DriverPerformance> findByDriverIdAndPeriodBetween(@Param("driverId") String driverId, 
                                                          @Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(dp.averageRating) FROM DriverPerformance dp WHERE dp.driverId = :driverId")
    Optional<Double> findAverageRatingByDriverId(@Param("driverId") String driverId);
    
    @Query("SELECT SUM(dp.totalDeliveries) FROM DriverPerformance dp WHERE dp.driverId = :driverId")
    Optional<Integer> findTotalDeliveriesByDriverId(@Param("driverId") String driverId);
    
    @Query("SELECT SUM(dp.earnings) FROM DriverPerformance dp WHERE dp.driverId = :driverId")
    Optional<Double> findTotalEarningsByDriverId(@Param("driverId") String driverId);
}