package com.sep.web_shop.back.feature_vehicle.dto;

import com.sep.web_shop.back.feature_vehicle.enumeration.VehicleType;

import java.math.BigDecimal;

public record VehicleDTO(
        Long id,
        String name,
        String description,
        VehicleType type,
        BigDecimal pricePerDay,
        String imagePath
) {
}
