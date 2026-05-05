package com.sep.web_shop.back.feature_reservation.service.interf;

import com.sep.web_shop.back.feature_reservation.dto.CreateReservationDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationDetailsDTO;
import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;

import java.util.List;
import java.util.Optional;

public interface ReservationService {

    List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId);

    Optional<ReservationDetailsDTO> createReservation(CreateReservationDTO createReservationDTO, String username);

}
