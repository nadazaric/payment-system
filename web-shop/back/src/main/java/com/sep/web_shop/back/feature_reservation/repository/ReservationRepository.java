package com.sep.web_shop.back.feature_reservation.repository;

import com.sep.web_shop.back.feature_reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long vehicleId,
            java.time.LocalDate endDate,
            java.time.LocalDate startDate
    );

    List<Reservation> findByVehicleId(Long vehicleId);

    List<Reservation> findByUserUsernameOrderByStartDateDesc(String username);

    List<Reservation> findByVehicleIdOrderByStartDateDesc(Long vehicleId);

}
