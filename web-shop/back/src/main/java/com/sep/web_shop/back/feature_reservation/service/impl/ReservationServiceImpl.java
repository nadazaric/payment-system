package com.sep.web_shop.back.feature_reservation.service.impl;

import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_auth.repository.UserRepository;
import com.sep.web_shop.back.feature_reservation.dto.CreateReservationDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationDetailsDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationHistoryDTO;
import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;
import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;
import com.sep.web_shop.back.feature_reservation.mapper.ReservationMapper;
import com.sep.web_shop.back.feature_reservation.model.Reservation;
import com.sep.web_shop.back.feature_reservation.repository.ReservationRepository;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationService;
import com.sep.web_shop.back.feature_vehicle.model.AdditionalService;
import com.sep.web_shop.back.feature_vehicle.model.InsurancePackage;
import com.sep.web_shop.back.feature_vehicle.model.Vehicle;
import com.sep.web_shop.back.feature_vehicle.repository.AdditionalServiceRepository;
import com.sep.web_shop.back.feature_vehicle.repository.InsurancePackageRepository;
import com.sep.web_shop.back.feature_vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    @Autowired ReservationRepository reservationRepository;
    @Autowired VehicleRepository vehicleRepository;
    @Autowired UserRepository userRepository;
    @Autowired InsurancePackageRepository insurancePackageRepository;
    @Autowired AdditionalServiceRepository additionalServiceRepository;
    @Autowired ReservationMapper reservationMapper;

    @Override
    public List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId) {
        return reservationMapper.toUnavailablePeriodDtoList(
                reservationRepository.findByVehicleId(vehicleId)
        );
    }

    @Override
    public Optional<ReservationDetailsDTO> createReservation(
            CreateReservationDTO createReservationDTO,
            String username
    ) {
        if (
                createReservationDTO.startDate() == null
                        || createReservationDTO.endDate() == null
                        || !createReservationDTO.endDate().isAfter(createReservationDTO.startDate())
        ) {
            return Optional.empty();
        }

        boolean vehicleUnavailable = reservationRepository
                .existsByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        createReservationDTO.vehicleId(),
                        createReservationDTO.endDate(),
                        createReservationDTO.startDate()
                );

        if (vehicleUnavailable) {
            return Optional.empty();
        }

        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(createReservationDTO.vehicleId());
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<InsurancePackage> insurancePackageOptional = insurancePackageRepository.findById(createReservationDTO.insurancePackageId());

        if (
                vehicleOptional.isEmpty()
                        || userOptional.isEmpty()
                        || insurancePackageOptional.isEmpty()
        ) {
            return Optional.empty();
        }

        Vehicle vehicle = vehicleOptional.get();
        User user = userOptional.get();
        InsurancePackage insurancePackage = insurancePackageOptional.get();

        List<Long> additionalServiceIds = createReservationDTO.additionalServiceIds() == null
                ? List.of()
                : createReservationDTO.additionalServiceIds();

        Set<AdditionalService> additionalServices = new HashSet<>(
                additionalServiceRepository.findAllById(additionalServiceIds)
        );

        if (additionalServices.size() != additionalServiceIds.size()) {
            return Optional.empty();
        }

        long numberOfDays = ChronoUnit.DAYS.between(
                createReservationDTO.startDate(),
                createReservationDTO.endDate()
        );

        BigDecimal additionalServicesPricePerDay = additionalServices.stream()
                .map(AdditionalService::getPricePerDay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPerDay = vehicle.getPricePerDay()
                .add(insurancePackage.getPricePerDay())
                .add(additionalServicesPricePerDay);

        BigDecimal totalPrice = totalPerDay.multiply(BigDecimal.valueOf(numberOfDays));

        Reservation reservation = new Reservation();
        reservation.setVehicle(vehicle);
        reservation.setUser(user);
        reservation.setInsurancePackage(insurancePackage);
        reservation.setAdditionalServices(additionalServices);
        reservation.setStartDate(createReservationDTO.startDate());
        reservation.setEndDate(createReservationDTO.endDate());
        reservation.setTotalPrice(totalPrice);
        reservation.setPaymentStatus(PaymentStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);

        return Optional.of(reservationMapper.toDetailsDTO(savedReservation));
    }

    @Override
    public List<ReservationHistoryDTO> getReservations(String username) {
        return reservationMapper.toHistoryDtoList(
                reservationRepository.findByUserUsernameOrderByStartDateDesc(username)
        );
    }

}