package com.sep.web_shop.back.feature_vehicle.model;

import com.sep.web_shop.back.feature_vehicle.enumeration.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private BigDecimal pricePerDay;

    private String imagePath;

}
