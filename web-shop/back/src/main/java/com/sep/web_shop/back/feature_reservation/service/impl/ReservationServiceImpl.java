package com.sep.web_shop.back.feature_reservation.service.impl;

import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;
import com.sep.web_shop.back.feature_reservation.mapper.ReservationMapper;
import com.sep.web_shop.back.feature_reservation.repository.ReservationRepository;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId) {
        return reservationMapper.toUnavailablePeriodDtoList(
                reservationRepository.findByVehicleId(vehicleId)
        );
    }

}