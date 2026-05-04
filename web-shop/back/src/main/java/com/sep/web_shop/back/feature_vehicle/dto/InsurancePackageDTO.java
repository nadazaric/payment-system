package com.sep.web_shop.back.feature_vehicle.dto;

import java.math.BigDecimal;

public record InsurancePackageDTO(
        Long id,
        String name,
        String description,
        BigDecimal pricePerDay
) {
}
