package com.sep.web_shop.back.feature_vehicle.repository;

import com.sep.web_shop.back.feature_vehicle.dto.VehicleOptionsDTO;
import com.sep.web_shop.back.feature_vehicle.mapper.VehicleOptionsMapper;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleOptionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleOptionsServiceImpl implements VehicleOptionsService {

    @Autowired InsurancePackageRepository insurancePackageRepository;
    @Autowired AdditionalServiceRepository additionalServiceRepository;
    @Autowired VehicleOptionsMapper vehicleOptionsMapper;

    @Override
    public VehicleOptionsDTO getVehicleOptions() {
        return new VehicleOptionsDTO(
                vehicleOptionsMapper.toInsurancePackageDtoList(insurancePackageRepository.findAll()),
                vehicleOptionsMapper.toAdditionalServiceDtoList(additionalServiceRepository.findAll())
        );
    }

}
