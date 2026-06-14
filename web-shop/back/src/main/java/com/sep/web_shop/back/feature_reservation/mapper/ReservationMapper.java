package com.sep.web_shop.back.feature_reservation.mapper;

import com.sep.web_shop.back.feature_reservation.dto.ReservationDetailsDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationHistoryDTO;
import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;
import com.sep.web_shop.back.feature_reservation.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    UnavailablePeriodDTO toUnavailablePeriodDto(Reservation reservation);

    List<UnavailablePeriodDTO> toUnavailablePeriodDtoList(List<Reservation> reservations);

    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "insurancePackage.id", target = "insurancePackageId")
    @Mapping(
            target = "additionalServiceIds",
            expression = "java(reservation.getAdditionalServices().stream().map(service -> service.getId()).toList())"
    )
    ReservationDetailsDTO toDetailsDTO(Reservation reservation);

    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.name", target = "vehicleName")
    @Mapping(source = "vehicle.imagePath", target = "vehicleImagePath")
    @Mapping(source = "vehicle.type", target = "vehicleType")
    @Mapping(source = "insurancePackage.name", target = "insurancePackageName")
    @Mapping(
            target = "additionalServiceNames",
            expression = "java(reservation.getAdditionalServices().stream().map(service -> service.getName()).toList())"
    )
    @Mapping(source = "paymentMethodCode", target = "paymentMethodCode")
    ReservationHistoryDTO toHistoryDTO(Reservation reservation);

    List<ReservationHistoryDTO> toHistoryDtoList(List<Reservation> reservations);

}
