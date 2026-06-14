package com.sep.bank.plugin.back.feature_psp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "merchant_id",
                        "seller_reference",
                        "payment_method_code"
                }
        )
)
public class BankPluginSellerConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String sellerReference;

    @Column(nullable = false)
    private String paymentMethodCode;

    @Column(nullable = false)
    private String bankMerchantId;

}
