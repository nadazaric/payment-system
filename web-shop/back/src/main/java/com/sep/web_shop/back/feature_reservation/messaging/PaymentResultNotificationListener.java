package com.sep.web_shop.back.feature_reservation.messaging;

import com.sep.web_shop.back.feature_reservation.event.PaymentResultNotificationEvent;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationPaymentNotificationService;
import com.sep.web_shop.back.shared.logging.LogStrings;
import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultNotificationListener {

    @Autowired
    ReservationPaymentNotificationService reservationPaymentNotificationService;

    @Autowired
    AppLoggerService appLoggerService;

    @RabbitListener(
            queues = "${app.rabbitmq.payment-notifications.queue}",
            containerFactory = "paymentNotificationsListenerContainerFactory"
    )
    public void handlePaymentResultNotification(PaymentResultNotificationEvent event) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_RECEIVED,
                "paymentId={} merchantId={} sellerReference={} merchantOrderId={} status={} paymentMethodCode={}",
                event.paymentId(),
                event.merchantId(),
                event.sellerReference(),
                event.merchantOrderId(),
                event.status(),
                event.paymentMethodCode()
        );

        try {
            reservationPaymentNotificationService.processPaymentResult(event);

            appLoggerService.info(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_NOTIFICATION_PROCESSED,
                    "paymentId={} merchantOrderId={} status={}",
                    event.paymentId(),
                    event.merchantOrderId(),
                    event.status()
            );
        } catch (Exception exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                    "paymentId={} merchantOrderId={} status={} error={}",
                    event.paymentId(),
                    event.merchantOrderId(),
                    event.status(),
                    exception.getMessage()
            );
        }
    }

}