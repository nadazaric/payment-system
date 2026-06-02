package com.sep.psp.back.feature_payment.model;

import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_code", nullable = false)
    private PaymentPlugin plugin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String configSchemaJson;

    public PaymentMethod(
            String code,
            String displayName,
            PaymentPlugin plugin,
            String configSchemaJson
    ) {
        this.code = code;
        this.displayName = displayName;
        this.plugin = plugin;
        this.configSchemaJson = configSchemaJson;
        this.active = true;
    }

}