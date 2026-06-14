package com.sep.web_shop.back.feature_reservation.repository;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;
import com.sep.web_shop.back.feature_reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndPaymentStatusIn(
            Long vehicleId,
            LocalDate endDate,
            LocalDate startDate,
            Collection<PaymentStatus> paymentStatuses
    );

    List<Reservation> findByVehicleIdAndPaymentStatusIn(
            Long vehicleId,
            Collection<PaymentStatus> paymentStatuses
    );

    List<Reservation> findByUserUsernameOrderByStartDateDesc(String username);

    List<Reservation> findByVehicleIdOrderByStartDateDesc(Long vehicleId);

    Optional<Reservation> findByPspPaymentIdAndMerchantOrderId(String pspPaymentId, String merchantOrderId);

}
