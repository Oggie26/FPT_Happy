package com.example.driverservice.repository;

import com.example.driverservice.model.Driver;
import com.example.driverservice.model.DriverStatus;
import com.example.driverservice.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByDriverId(String driverId);
    
    Optional<Driver> findByUserId(String userId);
    
    List<Driver> findByStatus(DriverStatus status);
    
    List<Driver> findByIsAvailable(Boolean isAvailable);
    
    List<Driver> findByVehicleType(VehicleType vehicleType);
    
    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.status = 'ACTIVE'")
    List<Driver> findAvailableDrivers();
    
    @Query("SELECT d FROM Driver d WHERE d.rating >= :minRating")
    List<Driver> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);
    
    @Query("SELECT d FROM Driver d WHERE d.totalDeliveries >= :minDeliveries")
    List<Driver> findByTotalDeliveriesGreaterThanEqual(@Param("minDeliveries") Integer minDeliveries);
    
    @Query("SELECT d FROM Driver d WHERE d.currentLatitude BETWEEN :minLat AND :maxLat AND d.currentLongitude BETWEEN :minLng AND :maxLng")
    List<Driver> findDriversInArea(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                   @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);
    
    boolean existsByDriverId(String driverId);
    
    boolean existsByUserId(String userId);
    
    boolean existsByLicenseNumber(String licenseNumber);
}