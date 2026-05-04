package com.sep.web_shop.back.feature_vehicle.mapper;

import com.sep.web_shop.back.feature_vehicle.dto.AdditionalServiceDTO;
import com.sep.web_shop.back.feature_vehicle.dto.InsurancePackageDTO;
import com.sep.web_shop.back.feature_vehicle.model.AdditionalService;
import com.sep.web_shop.back.feature_vehicle.model.InsurancePackage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleOptionsMapper {

    InsurancePackageDTO toInsurancePackageDto(InsurancePackage insurancePackage);

    List<InsurancePackageDTO> toInsurancePackageDtoList(List<InsurancePackage> insurancePackages);

    AdditionalServiceDTO toAdditionalServiceDto(AdditionalService additionalService);

    List<AdditionalServiceDTO> toAdditionalServiceDtoList(List<AdditionalService> additionalServices);

}
