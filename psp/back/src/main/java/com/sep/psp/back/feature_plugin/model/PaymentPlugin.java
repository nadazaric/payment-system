package com.sep.psp.back.feature_plugin.model;

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
public class PaymentPlugin {

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String baseUrl;

    @Column(nullable = false)
    private boolean active;

    public PaymentPlugin(
            String code,
            String displayName,
            String baseUrl
    ) {
        this.code = code;
        this.displayName = displayName;
        this.baseUrl = baseUrl;
        this.active = true;
    }

}