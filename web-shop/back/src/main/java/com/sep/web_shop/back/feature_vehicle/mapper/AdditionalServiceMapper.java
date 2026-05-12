package com.sep.web_shop.back.feature_vehicle.mapper;

import com.sep.web_shop.back.feature_vehicle.dto.AdditionalServiceDTO;
import com.sep.web_shop.back.feature_vehicle.model.AdditionalService;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionalServiceMapper {

    AdditionalServiceDTO toDto(AdditionalService additionalService);

    List<AdditionalServiceDTO> toDtoList(List<AdditionalService> additionalServices);

}
