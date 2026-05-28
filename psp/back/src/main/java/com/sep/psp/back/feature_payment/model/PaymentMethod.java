package com.sep.psp.back.feature_payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PaymentMethod {

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean active;

    public PaymentMethod(
            String code,
            String displayName
    ) {
        this.code = code;
        this.displayName = displayName;
        this.active = true;
    }

}