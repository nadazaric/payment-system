package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_payment.dto.*;
import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import com.sep.psp.back.feature_payment.mapper.PaymentTransactionMapper;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.repository.PaymentTransactionRepository;
import com.sep.psp.back.feature_payment.service.interf.PaymentTransactionService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppLoggerService appLoggerService;

    @Autowired
    PaymentTransactionMapper paymentTransactionMapper;

    @Value("${app.psp.payment-page-base-url}")
    String paymentPageBaseUrl;

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        validateAmount(request.amount());

        Merchant merchant = getMerchantOrThrow(request.merchantId());

        validateMerchantPassword(merchant, request.merchantPassword());

        validateMerchantIsActive(merchant);

        validateCurrency(merchant, request.currency());

        validateMerchantOrderIsUnique(merchant, request.merchantOrderId());

        MerchantSellerAccount sellerAccount = getSellerAccountOrThrow(merchant, request.sellerReference());

        validateSellerIsActive(sellerAccount);

        PaymentTransaction paymentTransaction = new PaymentTransaction(
                merchant,
                sellerAccount,
                request.amount(),
                request.currency(),
                request.merchantOrderId(),
                request.merchantTimestamp()
        );

        PaymentTransaction savedPaymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CREATED,
                "paymentId={} merchantId={} sellerReference={} merchantOrderId={} amount={} currency={}",
                savedPaymentTransaction.getId(),
                merchant.getMerchantId(),
                sellerAccount.getSellerReference(),
                savedPaymentTransaction.getMerchantOrderId(),
                savedPaymentTransaction.getAmount(),
                savedPaymentTransaction.getCurrency()
        );

        return new CreatePaymentResponse(
                savedPaymentTransaction.getId(),
                buildPaymentRedirectUrl(savedPaymentTransaction.getId()),
                savedPaymentTransaction.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPayment(String paymentId) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment transaction not found."));

        return paymentTransactionMapper.toResponse(paymentTransaction);
    }

    @Override
    @Transactional
    public InitiatePaymentResponse initiatePayment(String paymentId, InitiatePaymentRequest request) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment transaction not found."));

        validatePaymentCanBeInitiated(
                paymentTransaction,
                request.paymentMethodCode()
        );

        MerchantSellerPaymentMethod sellerPaymentMethod = getAvailableSellerPaymentMethodOrThrow(
                paymentTransaction,
                request.paymentMethodCode()
        );

        paymentTransaction.setSelectedPaymentMethodCode(
                sellerPaymentMethod.getPaymentMethod().getCode()
        );
        paymentTransaction.setStatus(PaymentStatus.INITIATED);

        PaymentTransaction savedPaymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_INITIATED,
                "paymentId={} merchantId={} sellerReference={} selectedPaymentMethodCode={}",
                savedPaymentTransaction.getId(),
                savedPaymentTransaction.getMerchant().getMerchantId(),
                savedPaymentTransaction.getSellerAccount().getSellerReference(),
                savedPaymentTransaction.getSelectedPaymentMethodCode()
        );

        return new InitiatePaymentResponse(
                savedPaymentTransaction.getId(),
                savedPaymentTransaction.getSelectedPaymentMethodCode(),
                savedPaymentTransaction.getStatus(),
                null
        );
    }

    private Merchant getMerchantOrThrow(String merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_CREATE_REJECTED,
                            "reason={} merchantId={}",
                            LogStrings.Reason.MERCHANT_NOT_FOUND,
                            merchantId
                    );

                    return new BadRequestException("Merchant not found.");
                });
    }

    private MerchantSellerAccount getSellerAccountOrThrow(
            Merchant merchant,
            String sellerReference
    ) {
        return merchantSellerAccountRepository.findByMerchantAndSellerReference(
                        merchant,
                        sellerReference
                )
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_CREATE_REJECTED,
                            "reason={} merchantId={} sellerReference={}",
                            LogStrings.Reason.SELLER_NOT_FOUND,
                            merchant.getMerchantId(),
                            sellerReference
                    );

                    return new BadRequestException("Seller account not found.");
                });
    }

    private void validateMerchantPassword(
            Merchant merchant,
            String merchantPassword
    ) {
        if (!passwordEncoder.matches(
                merchantPassword,
                merchant.getMerchantPasswordHash()
        )) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} merchantId={}",
                    LogStrings.Reason.INVALID_MERCHANT_CREDENTIALS,
                    merchant.getMerchantId()
            );

            throw new BadRequestException("Invalid merchant credentials.");
        }
    }

    private void validateMerchantIsActive(Merchant merchant) {
        if (!merchant.isActive()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} merchantId={}",
                    LogStrings.Reason.INACTIVE_MERCHANT,
                    merchant.getMerchantId()
            );

            throw new BadRequestException("Merchant is not active.");
        }
    }

    private void validateSellerIsActive(MerchantSellerAccount sellerAccount) {
        if (!sellerAccount.isActive()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} sellerId={} sellerReference={}",
                    LogStrings.Reason.INACTIVE_SELLER,
                    sellerAccount.getId(),
                    sellerAccount.getSellerReference()
            );

            throw new BadRequestException("Seller is not active.");
        }
    }

    private void validateCurrency(
            Merchant merchant,
            String currency
    ) {
        if (!merchant.getCurrency().equals(currency)) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} merchantId={} expectedCurrency={} receivedCurrency={}",
                    LogStrings.Reason.INVALID_CURRENCY,
                    merchant.getMerchantId(),
                    merchant.getCurrency(),
                    currency
            );

            throw new BadRequestException("Invalid payment currency.");
        }
    }

    private void validateMerchantOrderIsUnique(
            Merchant merchant,
            String merchantOrderId
    ) {
        boolean exists = paymentTransactionRepository.existsByMerchantAndMerchantOrderId(
                merchant,
                merchantOrderId
        );

        if (exists) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} merchantId={} merchantOrderId={}",
                    LogStrings.Reason.DUPLICATE_MERCHANT_ORDER,
                    merchant.getMerchantId(),
                    merchantOrderId
            );

            throw new BadRequestException("Payment already exists for this merchant order.");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero.");
        }
    }

    private String buildPaymentRedirectUrl(String paymentId) {
        return paymentPageBaseUrl + "/" + paymentId;
    }

    private void validatePaymentCanBeInitiated(
            PaymentTransaction paymentTransaction,
            String paymentMethodCode
    ) {
        if (paymentTransaction.getStatus() == PaymentStatus.CREATED) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_INITIATE_REJECTED,
                "reason={} paymentId={} currentStatus={} requestedPaymentMethodCode={}",
                LogStrings.Reason.PAYMENT_ALREADY_INITIATED,
                paymentTransaction.getId(),
                paymentTransaction.getStatus(),
                paymentMethodCode
        );

        throw new BadRequestException("Payment cannot be initiated.");
    }

    private MerchantSellerPaymentMethod getAvailableSellerPaymentMethodOrThrow(PaymentTransaction paymentTransaction, String paymentMethodCode) {
        return paymentTransaction.getSellerAccount()
                .getPaymentMethods()
                .stream()
                .filter(MerchantSellerPaymentMethod::isAvailableForPayments)
                .filter(sellerPaymentMethod -> sellerPaymentMethod.getPaymentMethod()
                        .getCode()
                        .equals(paymentMethodCode))
                .findFirst()
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_INITIATE_REJECTED,
                            "reason={} paymentId={} sellerReference={} selectedPaymentMethodCode={}",
                            LogStrings.Reason.PAYMENT_METHOD_NOT_AVAILABLE,
                            paymentTransaction.getId(),
                            paymentTransaction.getSellerAccount().getSellerReference(),
                            paymentMethodCode
                    );

                    return new BadRequestException("Payment method is not available.");
                });
    }

}