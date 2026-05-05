package com.sep.web_shop.back.feature_vehicle.mapper;

import com.sep.web_shop.back.feature_vehicle.dto.CreateVehicleDTO;
import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;
import com.sep.web_shop.back.feature_vehicle.model.Vehicle;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDTO toDto(Vehicle vehicle);

    List<VehicleDTO> toDtoList(List<Vehicle> vehicles);

    Vehicle toEntity(CreateVehicleDTO createVehicleDTO);

}
