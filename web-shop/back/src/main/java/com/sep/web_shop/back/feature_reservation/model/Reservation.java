package com.sep.web_shop.back.feature_reservation.model;

import com.sep.web_shop.back.feature_auth.model.User;
import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;
import com.sep.web_shop.back.feature_vehicle.model.AdditionalService;
import com.sep.web_shop.back.feature_vehicle.model.InsurancePackage;
import com.sep.web_shop.back.feature_vehicle.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_package_id", nullable = false)
    private InsurancePackage insurancePackage;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "additional_service_id")
    )
    private Set<AdditionalService> additionalServices = new HashSet<>();

}
