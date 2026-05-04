package com.sep.web_shop.back.feature_reservation.service.interf;

import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;

import java.util.List;

public interface ReservationService {

    List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId);

}
