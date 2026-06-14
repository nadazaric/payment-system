package com.sep.bank.back.feature_payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Merchant {

    @Id
    @Column(nullable = false, updatable = false)
    private String bankMerchantId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BankAccount bankAccount;

}
