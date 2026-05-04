package com.sep.web_shop.back.feature_vehicle.service.impl;

import com.sep.web_shop.back.feature_vehicle.dto.InsurancePackageDTO;
import com.sep.web_shop.back.feature_vehicle.mapper.InsurancePackageMapper;
import com.sep.web_shop.back.feature_vehicle.repository.InsurancePackageRepository;
import com.sep.web_shop.back.feature_vehicle.service.interf.InsurancePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsurancePackageServiceImpl implements InsurancePackageService {

    private final InsurancePackageRepository insurancePackageRepository;
    private final InsurancePackageMapper insurancePackageMapper;

    @Override
    public List<InsurancePackageDTO> getAll() {
        return insurancePackageMapper.toDtoList(insurancePackageRepository.findAll());
    }

}
