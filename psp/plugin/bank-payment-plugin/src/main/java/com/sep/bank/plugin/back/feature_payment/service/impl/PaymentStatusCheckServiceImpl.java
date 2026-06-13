package com.sep.bank.plugin.back.feature_payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_payment.client.BankClient;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentStatusCheckRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentStatusCheckResponse;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PspPaymentStatusCheckRequest;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PspPaymentStatusCheckResponse;
import com.sep.bank.plugin.back.feature_payment.enumeration.PaymentResultDeliveryStatus;
import com.sep.bank.plugin.back.feature_payment.enumeration.PluginPaymentStatus;
import com.sep.bank.plugin.back.feature_payment.model.PluginPayment;
import com.sep.bank.plugin.back.feature_payment.repository.PluginPaymentRepository;
import com.sep.bank.plugin.back.feature_payment.service.interf.PaymentStatusCheckService;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import com.sep.bank.plugin.back.shared.security.service.interf.HmacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PaymentStatusCheckServiceImpl implements PaymentStatusCheckService {

    @Autowired
    PluginPaymentRepository pluginPaymentRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Autowired
    BankClient bankClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HmacService hmacService;

    @Value("${app.security.bank-secret}")
    String bankSecret;

    @Override
    @Transactional
    public PspPaymentStatusCheckResponse checkPaymentStatus(PspPaymentStatusCheckRequest request) {
        PluginPayment pluginPayment = findPluginPayment(request.paymentId());

        boolean hasReceivedBankResult = !PaymentResultDeliveryStatus.WAITING_BANK_RESULT.equals(pluginPayment.getResultDeliveryStatus());

        if (hasReceivedBankResult) {
            pluginPayment.setResultDeliveryStatus(PaymentResultDeliveryStatus.DELIVERED_TO_PSP);
            pluginPaymentRepository.save(pluginPayment);

            appLoggerService.info(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_STATUS_CHECK_COMPLETED,
                    "paymentId={} status={} resultDeliveryStatus={}",
                    pluginPayment.getPspPaymentId(),
                    pluginPayment.getStatus(),
                    pluginPayment.getResultDeliveryStatus()
            );

            return buildResponse(pluginPayment);
        }

        BankPaymentStatusCheckResponse bankResponse = checkPaymentStatusInBank(pluginPayment);
        updatePluginPaymentFromBankStatusCheck(pluginPayment, bankResponse);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_STATUS_CHECK_COMPLETED,
                "paymentId={} status={} resultDeliveryStatus={}",
                pluginPayment.getPspPaymentId(),
                pluginPayment.getStatus(),
                pluginPayment.getResultDeliveryStatus()
        );

        return buildResponse(pluginPayment);
    }

    private PluginPayment findPluginPayment(String paymentId) {
        return pluginPaymentRepository.findByPspPaymentId(paymentId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_STATUS_CHECK_REJECTED,
                            "reason={} paymentId={}",
                            LogStrings.Reason.PLUGIN_PAYMENT_NOT_FOUND,
                            paymentId
                    );

                    return new IllegalArgumentException("Plugin payment was not found.");
                });
    }

    private PspPaymentStatusCheckResponse buildResponse(PluginPayment pluginPayment) {
        return new PspPaymentStatusCheckResponse(
                pluginPayment.getPspPaymentId(),
                pluginPayment.getStatus(),
                pluginPayment.getResultMessage()
        );
    }

    private BankPaymentStatusCheckResponse checkPaymentStatusInBank(PluginPayment pluginPayment) {
        BankPaymentStatusCheckRequest bankRequest = new BankPaymentStatusCheckRequest(
                pluginPayment.getBankMerchantId(),
                pluginPayment.getStan(),
                pluginPayment.getPspTimestamp()
        );

        String requestBody = writeJson(bankRequest);
        String timestamp = Instant.now().toString();

        String signature = hmacService.generateSignature(
                bankSecret,
                timestamp,
                requestBody
        );

        return bankClient.checkPaymentStatus(
                timestamp,
                signature,
                requestBody
        );
    }

    private void updatePluginPaymentFromBankStatusCheck(
            PluginPayment pluginPayment,
            BankPaymentStatusCheckResponse bankResponse
    ) {
        PluginPaymentStatus bankStatus = PluginPaymentStatus.valueOf(bankResponse.status());

        pluginPayment.setStatus(bankStatus);
        pluginPayment.setResultMessage(bankResponse.message());

        if (!PluginPaymentStatus.INITIATED.equals(bankStatus)) {
            pluginPayment.setGlobalTransactionId(bankResponse.globalTransactionId());
            pluginPayment.setAcquirerTimestamp(bankResponse.acquirerTimestamp());
            pluginPayment.setResultDeliveryStatus(PaymentResultDeliveryStatus.DELIVERED_TO_PSP);
        }

        pluginPaymentRepository.save(pluginPayment);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(LogStrings.Reason.JSON_SERIALIZATION_FAILED);
        }
    }

}