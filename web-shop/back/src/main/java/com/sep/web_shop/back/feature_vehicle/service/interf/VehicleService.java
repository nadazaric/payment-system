package com.sep.web_shop.back.feature_vehicle.service.interf;

import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    List<VehicleDTO> getAll();

    Optional<VehicleDTO> getById(Long id);

}
