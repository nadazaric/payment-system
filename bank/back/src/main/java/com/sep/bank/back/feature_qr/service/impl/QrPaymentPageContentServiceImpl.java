package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.model.Merchant;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_qr.dto.QrPaymentPageContentDTO;
import com.sep.bank.back.feature_qr.service.interf.IpsQrImageService;
import com.sep.bank.back.feature_qr.service.interf.IpsQrPayloadService;
import com.sep.bank.back.feature_qr.service.interf.QrPaymentPageContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QrPaymentPageContentServiceImpl implements QrPaymentPageContentService {

    private static final String PAYMENT_CODE = "221";
    private static final String PAYMENT_PURPOSE = "Online payment";

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    IpsQrPayloadService ipsQrPayloadService;

    @Autowired
    IpsQrImageService ipsQrImageService;

    @Override
    @Transactional(readOnly = true)
    public QrPaymentPageContentDTO buildContent(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment does not exist."));

        validateQrPayment(payment);

        Merchant merchant = merchantRepository.findByBankMerchantId(payment.getBankMerchantId())
                .orElseThrow(() -> new IllegalArgumentException("Bank merchant does not exist."));

        String recipientAccount = normalizeRecipientAccount(
                merchant.getBankAccount().getAccountNumber()
        );

        String paymentPurpose = PAYMENT_PURPOSE + " " + payment.getStan();

        String payload = ipsQrPayloadService.buildPayload(
                recipientAccount,
                merchant.getName(),
                payment.getAmount(),
                payment.getCurrency(),
                PAYMENT_CODE,
                paymentPurpose,
                payment.getQrPaymentReference()
        );

        String qrImageBase64 = ipsQrImageService.generateBase64Png(payload);

        return new QrPaymentPageContentDTO(qrImageBase64);
    }

    private void validateQrPayment(Payment payment) {
        if (!PaymentMethod.QR.equals(payment.getPaymentMethod())) {
            throw new IllegalArgumentException("Payment method is not QR.");
        }

        if (payment.getQrPaymentReference() == null || payment.getQrPaymentReference().isBlank()) {
            throw new IllegalArgumentException("QR payment reference is missing.");
        }
    }

    private String normalizeRecipientAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Recipient account is missing.");
        }

        String digitsOnly = accountNumber.replaceAll("\\D", "");

        if (accountNumber.toUpperCase().startsWith("RS") && digitsOnly.length() == 20) {
            return digitsOnly.substring(2);
        }

        return digitsOnly;
    }

}