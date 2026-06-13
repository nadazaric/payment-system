package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.bank.back.feature_payment.dto.PaymentPageDTO;
import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.bank.payment-page-base-url}")
    String paymentPageBaseUrl;

    @Value("${app.bank.payment-url-expiration-minutes}")
    Long paymentUrlExpirationMinutes;

    private static final DateTimeFormatter REFERENCE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final int RANDOM_NUMBER_MIN = 1000;
    private static final int RANDOM_NUMBER_MAX = 9999;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CREATE_REQUEST_RECEIVED,
                "bankMerchantId={} stan={} pspTimestamp={} paymentMethod={} amount={} currency={}",
                request.bankMerchantId(),
                request.stan(),
                request.pspTimestamp(),
                request.paymentMethod(),
                request.amount(),
                request.currency()
        );

        validateMerchantExists(request.bankMerchantId());
        validateDuplicatePaymentRequest(request);

        Payment payment = new Payment();

        LocalDateTime now = LocalDateTime.now();

        payment.setBankMerchantId(request.bankMerchantId());
        payment.setStan(request.stan());
        payment.setPspTimestamp(request.pspTimestamp());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setSuccessUrl(request.successUrl());
        payment.setFailUrl(request.failUrl());
        payment.setErrorUrl(request.errorUrl());
        payment.setPluginCallbackUrl(request.pluginCallbackUrl());
        payment.setCreatedAt(now);
        payment.setExpiresAt(now.plusMinutes(paymentUrlExpirationMinutes));
        payment.setPaymentAttemptUsed(false);

        if (PaymentMethod.QR.equals(request.paymentMethod())) {
            payment.setQrPaymentReference(generateQrPaymentReference());
        }

        Payment savedPayment = paymentRepository.save(payment);
        String paymentUrl = paymentPageBaseUrl + "/" + savedPayment.getId();

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CREATED,
                "bankPaymentId={} bankMerchantId={} stan={} paymentUrl={} expiresAt={}",
                savedPayment.getId(),
                savedPayment.getBankMerchantId(),
                savedPayment.getStan(),
                paymentUrl,
                savedPayment.getExpiresAt()
        );

        return new CreatePaymentResponse(
                savedPayment.getId().toString(),
                paymentUrl
        );
    }

    private void validateMerchantExists(String bankMerchantId) {
        boolean merchantExists = merchantRepository.existsByBankMerchantId(bankMerchantId);

        if (!merchantExists) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} bankMerchantId={}",
                    LogStrings.Reason.BANK_MERCHANT_NOT_FOUND,
                    bankMerchantId
            );

            throw new IllegalArgumentException("Bank merchant does not exist.");
        }
    }

    private void validateDuplicatePaymentRequest(CreatePaymentRequest request) {
        boolean paymentExists = paymentRepository.existsByBankMerchantIdAndStanAndPspTimestamp(
                request.bankMerchantId(),
                request.stan(),
                request.pspTimestamp()
        );

        if (paymentExists) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} bankMerchantId={} stan={} pspTimestamp={}",
                    LogStrings.Reason.PAYMENT_REQUEST_ALREADY_EXISTS,
                    request.bankMerchantId(),
                    request.stan(),
                    request.pspTimestamp()
            );

            throw new IllegalArgumentException("Payment request already exists.");
        }
    }

    @Override
    public Optional<PaymentPageDTO> getPaymentPageData(UUID paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_PAGE_NOT_FOUND,
                    "reason={} bankPaymentId={}",
                    LogStrings.Reason.PAYMENT_NOT_FOUND,
                    paymentId
            );

            return Optional.empty();
        }

        Payment payment = paymentOptional.get();

        boolean expired = payment.getExpiresAt().isBefore(LocalDateTime.now());

        return Optional.of(new PaymentPageDTO(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getExpiresAt(),
                payment.getPaymentAttemptUsed(),
                expired
        ));
    }

    private String generateQrPaymentReference() {
        String timestampPart = LocalDateTime.now().format(REFERENCE_DATE_TIME_FORMATTER);

        while (true){
            int randomPart = ThreadLocalRandom.current().nextInt(RANDOM_NUMBER_MIN, RANDOM_NUMBER_MAX + 1);
            String reference = timestampPart + randomPart;

            if (!paymentRepository.existsByQrPaymentReference(reference)) {
                return reference;
            }
        }
    }

}