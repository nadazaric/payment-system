package com.sep.psp.back.feature_payment.scheduler;

import com.sep.psp.back.feature_payment.service.interf.PaymentStatusCheckService;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusCheckScheduler {

    @Autowired
    PaymentStatusCheckService paymentStatusCheckService;

    @Autowired
    AppLoggerService appLoggerService;

    @Scheduled(
            fixedDelayString = "${app.payment.status-check-delay-ms:60000}",
            initialDelayString = "${app.payment.status-check-initial-delay-ms:10000}"
    )
    public void checkInitiatedPayments() {
        try {
            appLoggerService.info(
                    LogStrings.Feature.SCHEDULING,
                    LogStrings.Action.STARTED,
                    ""
            );

            paymentStatusCheckService.checkInitiatedPayments();
            paymentStatusCheckService.expireCreatedPayments();
        } catch (Exception exception) {
            appLoggerService.error(
                    LogStrings.Feature.SCHEDULING,
                    LogStrings.Action.PAYMENT_STATUS_CHECK_SCHEDULER_FAILED,
                    "error={}",
                    exception.getMessage()
            );
        }
    }

}