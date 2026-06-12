package com.sep.bank.back.feature_payment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String pan;

    @Column(nullable = false)
    private String cardHolderName;

    @Column(nullable = false)
    private Integer expirationMonth;

    @Column(nullable = false)
    private Integer expirationYear;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private BankAccount bankAccount;

}