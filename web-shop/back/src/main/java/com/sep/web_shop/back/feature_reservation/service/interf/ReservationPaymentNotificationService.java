package com.sep.web_shop.back.feature_reservation.service.interf;

import com.sep.web_shop.back.feature_reservation.event.PaymentResultNotificationEvent;

public interface ReservationPaymentNotificationService {

    void processPaymentResult(PaymentResultNotificationEvent event);

}