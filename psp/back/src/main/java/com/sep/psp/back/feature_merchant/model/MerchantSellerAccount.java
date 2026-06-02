package com.sep.psp.back.feature_merchant.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
    private Boolean active;

    @OneToMany(
            mappedBy = "sellerAccount",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MerchantSellerPaymentMethod> paymentMethods = new ArrayList<>();

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

    public Boolean hasAvailablePaymentMethods() {
        return this.paymentMethods.stream()
                .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}