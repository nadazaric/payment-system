package com.sep.web_shop.back.feature_reservation.service.interf;

import com.sep.web_shop.back.feature_reservation.dto.CreateReservationDTO;
import com.sep.web_shop.back.feature_reservation.dto.CreateReservationResponse;
import com.sep.web_shop.back.feature_reservation.dto.ReservationHistoryDTO;
import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId);

    Optional<CreateReservationResponse> createReservation(CreateReservationDTO createReservationDTO, String username);

    List<ReservationHistoryDTO> getReservations(String username);

    List<ReservationHistoryDTO> getReservationsByVehicle(Long vehicleId);
}