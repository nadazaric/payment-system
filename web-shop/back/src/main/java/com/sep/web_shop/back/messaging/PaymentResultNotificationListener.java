package com.sep.web_shop.back.feature_reservation.messaging;

import com.sep.web_shop.back.feature_reservation.event.PaymentResultNotificationEvent;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationPaymentNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultNotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResultNotificationListener.class);

    @Autowired
    ReservationPaymentNotificationService reservationPaymentNotificationService;

    @RabbitListener(
            queues = "${app.rabbitmq.payment-notifications.queue}",
            containerFactory = "paymentNotificationsListenerContainerFactory"
    )
    public void handlePaymentResultNotification(PaymentResultNotificationEvent event) {
        try {
            reservationPaymentNotificationService.processPaymentResult(event);

            logger.info(
                    "Payment notification processed. paymentId={} merchantOrderId={} status={}",
                    event.paymentId(),
                    event.merchantOrderId(),
                    event.status()
            );
        } catch (Exception exception) {
            logger.error(
                    "Payment notification processing failed. paymentId={} merchantOrderId={} status={} error={}",
                    event.paymentId(),
                    event.merchantOrderId(),
                    event.status(),
                    exception.getMessage()
            );
        }
    }

}