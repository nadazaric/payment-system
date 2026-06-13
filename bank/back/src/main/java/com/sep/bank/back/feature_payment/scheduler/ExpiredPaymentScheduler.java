package com.sep.bank.back.feature_payment.scheduler;

import com.sep.bank.back.feature_payment.service.interf.ExpiredPaymentService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredPaymentScheduler {

    @Autowired
    ExpiredPaymentService expiredPaymentService;

    @Autowired
    AppLoggerService appLoggerService;

    @Scheduled(
            fixedDelayString = "${app.bank.expired-payment-check-delay-ms:60000}",
            initialDelayString = "${app.bank.expired-payment-check-initial-delay-ms:10000}"
    )
    public void processExpiredPayments() {
        try {
            appLoggerService.info(
                    LogStrings.Feature.SCHEDULING,
                    LogStrings.Action.STARTED,
                    ""
            );

            expiredPaymentService.processExpiredPayments();
        } catch (Exception exception) {
            appLoggerService.error(
                    LogStrings.Feature.SCHEDULING,
                    LogStrings.Action.EXPIRED_PAYMENT_CHECK_FAILED,
                    "error={}",
                    exception.getMessage()
            );
        }
    }

}