package com.sep.psp.back.feature_merchant.model;

import com.sep.psp.back.feature_payment.model.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MerchantSellerAccount {

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String sellerReference;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "merchant_seller_available_payment_methods",
            joinColumns = @JoinColumn(name = "merchant_seller_account_id"),
            inverseJoinColumns = @JoinColumn(name = "payment_method_code")
    )
    private List<PaymentMethod> availablePaymentMethods = new ArrayList<>();

    public MerchantSellerAccount(
            Merchant merchant,
            String sellerReference,
            String displayName
    ) {
        this.merchant = merchant;
        this.sellerReference = sellerReference;
        this.displayName = displayName;
        this.active = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}