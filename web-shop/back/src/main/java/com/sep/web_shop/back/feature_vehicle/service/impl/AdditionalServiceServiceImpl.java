package com.sep.web_shop.back.feature_vehicle.service.impl;

import com.sep.web_shop.back.feature_vehicle.dto.AdditionalServiceDTO;
import com.sep.web_shop.back.feature_vehicle.mapper.AdditionalServiceMapper;
import com.sep.web_shop.back.feature_vehicle.repository.AdditionalServiceRepository;
import com.sep.web_shop.back.feature_vehicle.service.interf.AdditionalServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceRepository additionalServiceRepository;
    private final AdditionalServiceMapper additionalServiceMapper;

    @Override
    public List<AdditionalServiceDTO> getAll() {
        return additionalServiceMapper.toDtoList(additionalServiceRepository.findAll());
    }

}
