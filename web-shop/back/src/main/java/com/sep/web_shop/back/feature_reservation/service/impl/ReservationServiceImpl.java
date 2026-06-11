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
import com.sep.web_shop.back.shared.logging.LogStrings;
import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
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

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional(readOnly = true)
    public List<UnavailablePeriodDTO> getUnavailablePeriods(Long vehicleId) {
        return reservationMapper.toUnavailablePeriodDtoList(
                reservationRepository.findByVehicleIdAndPaymentStatusIn(
                        vehicleId,
                        getVehicleBlockingPaymentStatuses()
                )
        );
    }

    @Override
    @Transactional
    public Optional<CreateReservationResponse> createReservation(CreateReservationDTO createReservationDTO, String username) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_REQUEST_RECEIVED,
                "username={} vehicleId={} startDate={} endDate={} insurancePackageId={} additionalServiceIds={}",
                username,
                createReservationDTO.vehicleId(),
                createReservationDTO.startDate(),
                createReservationDTO.endDate(),
                createReservationDTO.insurancePackageId(),
                createReservationDTO.additionalServiceIds()
        );

        if (createReservationDTO.startDate() == null || createReservationDTO.endDate() == null || !createReservationDTO.endDate().isAfter(createReservationDTO.startDate())) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} username={} vehicleId={} startDate={} endDate={}",
                    LogStrings.Reason.INVALID_RESERVATION_PERIOD,
                    username,
                    createReservationDTO.vehicleId(),
                    createReservationDTO.startDate(),
                    createReservationDTO.endDate()
            );

            return Optional.empty();
        }

        boolean vehicleUnavailable = reservationRepository
                .existsByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndPaymentStatusIn(
                        createReservationDTO.vehicleId(),
                        createReservationDTO.endDate(),
                        createReservationDTO.startDate(),
                        getVehicleBlockingPaymentStatuses()
                );

        if (vehicleUnavailable) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} username={} vehicleId={} startDate={} endDate={}",
                    LogStrings.Reason.VEHICLE_UNAVAILABLE,
                    username,
                    createReservationDTO.vehicleId(),
                    createReservationDTO.startDate(),
                    createReservationDTO.endDate()
            );

            return Optional.empty();
        }

        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(createReservationDTO.vehicleId());
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<InsurancePackage> insurancePackageOptional = insurancePackageRepository.findById(createReservationDTO.insurancePackageId());

        if (vehicleOptional.isEmpty() || userOptional.isEmpty() || insurancePackageOptional.isEmpty()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} username={} vehicleId={} insurancePackageId={} vehicleFound={} userFound={} insurancePackageFound={}",
                    LogStrings.Reason.MISSING_REQUIRED_RESERVATION_DATA,
                    username,
                    createReservationDTO.vehicleId(),
                    createReservationDTO.insurancePackageId(),
                    vehicleOptional.isPresent(),
                    userOptional.isPresent(),
                    insurancePackageOptional.isPresent()
            );

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
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} username={} additionalServiceIds={}",
                    LogStrings.Reason.ADDITIONAL_SERVICE_NOT_FOUND,
                    username,
                    additionalServiceIds
            );

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
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} username={} merchantOrderId={} amount={} currency={} error={}",
                    LogStrings.Reason.PSP_PAYMENT_CREATE_FAILED,
                    username,
                    merchantOrderId,
                    totalPrice,
                    pspCurrency,
                    exception.getMessage()
            );

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

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.RESERVATION_PAYMENT_INITIATED,
                "reservationId={} username={} vehicleId={} merchantOrderId={} pspPaymentId={} amount={} currency={} paymentStatus={} redirectUrl={}",
                savedReservation.getId(),
                savedReservation.getUser().getUsername(),
                savedReservation.getVehicle().getId(),
                savedReservation.getMerchantOrderId(),
                savedReservation.getPspPaymentId(),
                savedReservation.getTotalPrice(),
                pspCurrency,
                savedReservation.getPaymentStatus(),
                savedReservation.getPaymentRedirectUrl()
        );

        return Optional.of(new CreateReservationResponse(
                savedReservation.getId(),
                savedReservation.getPaymentRedirectUrl(),
                savedReservation.getPaymentStatus()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationHistoryDTO> getReservations(String username) {
        return reservationMapper.toHistoryDtoList(
                reservationRepository.findByUserUsernameOrderByStartDateDesc(username)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationHistoryDTO> getReservationsByVehicle(Long vehicleId) {
        return reservationMapper.toHistoryDtoList(
                reservationRepository.findByVehicleIdOrderByStartDateDesc(vehicleId)
        );
    }

    private List<PaymentStatus> getVehicleBlockingPaymentStatuses() {
        return List.of(
                PaymentStatus.INITIATED,
                PaymentStatus.SUCCESS
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