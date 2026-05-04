package com.sep.web_shop.back.feature_vehicle.mapper;

import com.sep.web_shop.back.feature_vehicle.dto.InsurancePackageDTO;
import com.sep.web_shop.back.feature_vehicle.model.InsurancePackage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InsurancePackageMapper {

    InsurancePackageDTO toDto(InsurancePackage insurancePackage);

    List<InsurancePackageDTO> toDtoList(List<InsurancePackage> insurancePackages);

}
