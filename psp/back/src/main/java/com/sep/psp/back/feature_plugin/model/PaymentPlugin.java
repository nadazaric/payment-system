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

    private String baseUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean registered;

    @Column(nullable = false)
    private String pluginSecret;

    public PaymentPlugin(
            String code,
            String displayName,
            String pluginSecret
    ) {
        this.code = code;
        this.displayName = displayName;
        this.pluginSecret = pluginSecret;
        this.active = true;
        this.registered = false;
    }

}