package com.sep.web_shop.back.feature_reservation.service.impl;

import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_auth.repository.UserRepository;
import com.sep.web_shop.back.feature_reservation.client.PspPaymentClient;
import com.sep.web_shop.back.feature_reservation.dto.CreateReservationDTO;
import com.sep.web_shop.back.feature_reservation.dto.CreateReservationResponse;
import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentRequest;
import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    InsurancePackageRepository insurancePackageRepository;

    @Autowired
    AdditionalServiceRepository additionalServiceRepository;

    @Autowired
    ReservationMapper reservationMapper;

    @Autowired
    PspPaymentClient pspPaymentClient;

    @Value("${app.psp.merchant-id}")
    String pspMerchantId;

    @Value("${app.psp.merchant-password}")
    String pspMerchantPassword;

    @Value("${app.psp.seller-reference}")
    String pspSellerReference;

    @Value("${app.psp.currency}")
    String pspCurrency;

    @Override
    public List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId) {
        return reservationMapper.toUnavailablePeriodDtoList(
                reservationRepository.findByVehicleId(vehicleId)
        );
    }

    @Override
    @Transactional
    public Optional<CreateReservationResponse> createReservation(
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

        BigDecimal totalPrice = calculateTotalPrice(
                createReservationDTO,
                vehicle,
                insurancePackage,
                additionalServices
        );

        String merchantOrderId = buildMerchantOrderId();

        PspCreatePaymentResponse pspResponse;

        try {
            pspResponse = createPspPayment(
                    totalPrice,
                    merchantOrderId
            );
        } catch (RestClientException exception) {
            return Optional.empty();
        }

        Reservation reservation = new Reservation();

        reservation.setVehicle(vehicle);
        reservation.setUser(user);
        reservation.setInsurancePackage(insurancePackage);
        reservation.setAdditionalServices(additionalServices);
        reservation.setStartDate(createReservationDTO.startDate());
        reservation.setEndDate(createReservationDTO.endDate());
        reservation.setTotalPrice(totalPrice);
        reservation.setPaymentStatus(PaymentStatus.INITIATED);
        reservation.setMerchantOrderId(merchantOrderId);
        reservation.setPspPaymentId(pspResponse.paymentId());
        reservation.setPaymentRedirectUrl(pspResponse.redirectUrl());

        Reservation savedReservation = reservationRepository.save(reservation);

        return Optional.of(new CreateReservationResponse(
                savedReservation.getId(),
                savedReservation.getPaymentRedirectUrl(),
                savedReservation.getPaymentStatus()
        ));
    }

    @Override
    public List<ReservationHistoryDTO> getReservations(String username) {
        return reservationMapper.toHistoryDtoList(
                reservationRepository.findByUserUsernameOrderByStartDateDesc(username)
        );
    }

    @Override
    public List<ReservationHistoryDTO> getReservationsByVehicle(Long vehicleId) {
        return reservationMapper.toHistoryDtoList(
                reservationRepository.findByVehicleIdOrderByStartDateDesc(vehicleId)
        );
    }

    private BigDecimal calculateTotalPrice(
            CreateReservationDTO createReservationDTO,
            Vehicle vehicle,
            InsurancePackage insurancePackage,
            Set<AdditionalService> additionalServices
    ) {
        long numberOfDays = ChronoUnit.DAYS.between(
                createReservationDTO.startDate(),
                createReservationDTO.endDate()
        ) + 1;

        BigDecimal additionalServicesPricePerDay = additionalServices.stream()
                .map(AdditionalService::getPricePerDay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPerDay = vehicle.getPricePerDay()
                .add(insurancePackage.getPricePerDay())
                .add(additionalServicesPricePerDay);

        return totalPerDay.multiply(BigDecimal.valueOf(numberOfDays));
    }

    private PspCreatePaymentResponse createPspPayment(
            BigDecimal totalPrice,
            String merchantOrderId
    ) {
        PspCreatePaymentRequest pspRequest = new PspCreatePaymentRequest(
                pspMerchantId,
                pspMerchantPassword,
                pspSellerReference,
                totalPrice,
                pspCurrency,
                merchantOrderId,
                LocalDateTime.now()
        );

        return pspPaymentClient.createPayment(pspRequest);
    }

    private String buildMerchantOrderId() {
        return "RES-" + UUID.randomUUID();
    }

}