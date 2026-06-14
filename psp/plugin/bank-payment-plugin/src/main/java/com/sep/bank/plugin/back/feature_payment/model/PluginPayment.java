package com.sep.bank.plugin.back.feature_payment.model;

import com.sep.bank.plugin.back.feature_payment.enumeration.PaymentResultDeliveryStatus;
import com.sep.bank.plugin.back.feature_payment.enumeration.PluginPaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class PluginPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String pspPaymentId;

    @Column(nullable = false)
    private String bankPaymentId;

    @Column(nullable = false, length = 1000)
    private String bankPaymentUrl;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String sellerReference;

    @Column(nullable = false)
    private String paymentMethodCode;

    @Column(nullable = false)
    private String bankMerchantId;

    @Column(nullable = false)
    private String stan;

    @Column(nullable = false)
    private LocalDateTime pspTimestamp;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PluginPaymentStatus status = PluginPaymentStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentResultDeliveryStatus resultDeliveryStatus = PaymentResultDeliveryStatus.WAITING_BANK_RESULT;

    private String globalTransactionId;

    private LocalDateTime acquirerTimestamp;

    private String resultMessage;

}