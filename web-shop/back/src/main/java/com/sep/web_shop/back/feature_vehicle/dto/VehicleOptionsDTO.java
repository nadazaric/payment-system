package com.sep.web_shop.back.feature_vehicle.dto;

import java.util.List;

public record VehicleOptionsDTO(
        List<InsurancePackageDTO> insurancePackages,
        List<AdditionalServiceDTO> additionalServices
) {
}
