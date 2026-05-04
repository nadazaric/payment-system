package com.sep.web_shop.back.feature_reservation.repository;

import com.sep.web_shop.back.feature_reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByVehicleId(Long vehicleId);

}
