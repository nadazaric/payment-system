package com.sep.web_shop.back.feature_vehicle.service.impl;

import com.sep.web_shop.back.feature_vehicle.dto.VehicleDTO;
import com.sep.web_shop.back.feature_vehicle.mapper.VehicleMapper;
import com.sep.web_shop.back.feature_vehicle.repository.VehicleRepository;
import com.sep.web_shop.back.feature_vehicle.service.interf.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    @Autowired VehicleRepository vehicleRepository;
    @Autowired VehicleMapper vehicleMapper;

    @Override
    public List<VehicleDTO> getAll() {
        return vehicleMapper.toDtoList(vehicleRepository.findAll());
    }

    @Override
    public Optional<VehicleDTO> getById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDto);
    }

}
