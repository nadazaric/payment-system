package com.sep.bank.back.feature_payment.model;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String bankMerchantId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String stan;

    @Column(nullable = false)
    private LocalDateTime pspTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, length = 1000)
    private String successUrl;

    @Column(nullable = false, length = 1000)
    private String failUrl;

    @Column(nullable = false, length = 1000)
    private String errorUrl;

    @Column(nullable = false, length = 1000)
    private String pluginCallbackUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean paymentAttemptUsed = false;

    private String globalTransactionId;

    private LocalDateTime acquirerTimestamp;

    @Column(unique = true, length = 32)
    private String qrPaymentReference;

}