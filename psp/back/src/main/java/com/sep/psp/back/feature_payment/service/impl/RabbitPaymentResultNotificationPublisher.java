package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_payment.event.PaymentResultNotificationEvent;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.service.interf.PaymentResultNotificationPublisher;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitPaymentResultNotificationPublisher implements PaymentResultNotificationPublisher {

    @Value("${app.rabbitmq.payment-notifications.exchange}")
    String paymentNotificationsExchange;

    @Value("${app.rabbitmq.payment-notifications.routing-key-template}")
    String paymentNotificationsRoutingKeyTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public void publishPaymentResult(
            PaymentTransaction paymentTransaction,
            String message
    ) {
        PaymentResultNotificationEvent event = new PaymentResultNotificationEvent(
                paymentTransaction.getId(),
                paymentTransaction.getMerchant().getMerchantId(),
                paymentTransaction.getSellerAccount().getSellerReference(),
                paymentTransaction.getMerchantOrderId(),
                paymentTransaction.getStatus(),
                paymentTransaction.getSelectedPaymentMethodCode(),
                message
        );

        String routingKey = buildRoutingKey(
                paymentTransaction.getMerchant().getMerchantId()
        );

        try {
            rabbitTemplate.convertAndSend(
                    paymentNotificationsExchange,
                    routingKey,
                    event
            );
        } catch (AmqpException exception) {
            appLoggerService.error(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_NOTIFICATION_PUBLISH_FAILED,
                    "reason={} paymentId={} merchantId={} routingKey={} error={}",
                    LogStrings.Reason.RABBITMQ_PUBLISH_FAILED,
                    paymentTransaction.getId(),
                    paymentTransaction.getMerchant().getMerchantId(),
                    routingKey,
                    exception.getMessage()
            );

            throw new IllegalStateException("Payment notification could not be published.");
        }
    }

    private String buildRoutingKey(String merchantId) {
        return String.format(
                paymentNotificationsRoutingKeyTemplate,
                merchantId
        );
    }

}