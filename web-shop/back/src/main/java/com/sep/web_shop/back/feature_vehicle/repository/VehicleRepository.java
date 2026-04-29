package com.sep.web_shop.back.feature_vehicle.repository;

import com.sep.web_shop.back.feature_vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
