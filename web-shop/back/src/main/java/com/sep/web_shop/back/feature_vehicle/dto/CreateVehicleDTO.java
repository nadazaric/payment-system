package com.sep.web_shop.back.feature_vehicle.dto;

import java.math.BigDecimal;

public record CreateVehicleDTO(
        String name,
        String description,
        String type,
        BigDecimal pricePerDay
) {
}
