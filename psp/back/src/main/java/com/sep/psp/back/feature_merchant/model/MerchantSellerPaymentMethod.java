package com.sep.psp.back.feature_merchant.model;

import com.sep.psp.back.feature_payment.model.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "seller_account_id",
                        "payment_method_code"
                }
        )
)
public class MerchantSellerPaymentMethod {

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_account_id", nullable = false)
    private MerchantSellerAccount sellerAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_code", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private boolean configured;

    public MerchantSellerPaymentMethod(MerchantSellerAccount sellerAccount, PaymentMethod paymentMethod, boolean configured) {
        this.sellerAccount = sellerAccount;
        this.paymentMethod = paymentMethod;
        this.configured = configured;
    }

    public boolean isAvailableForPayments() {
        return this.configured
                && this.paymentMethod.isActive()
                && this.paymentMethod.getPlugin().isActive();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}