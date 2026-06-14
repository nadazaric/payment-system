package com.sep.web_shop.back.shared.logging.service.impl;

import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

@Service
public class AppLoggerServiceImpl implements AppLoggerService {

    private static final Logger logger = LoggerFactory.getLogger(AppLoggerServiceImpl.class);

    private static final String FEATURE_KEY = "feature";
    private static final String ACTION_KEY = "action";

    @Override
    public void info(
            String feature,
            String action,
            String message,
            Object... args
    ) {
        log(
                feature,
                action,
                () -> logger.info(format(message, args))
        );
    }

    @Override
    public void warn(
            String feature,
            String action,
            String message,
            Object... args
    ) {
        log(
                feature,
                action,
                () -> logger.warn(format(message, args))
        );
    }

    @Override
    public void error(
            String feature,
            String action,
            String message,
            Object... args
    ) {
        log(
                feature,
                action,
                () -> logger.error(format(message, args))
        );
    }

    private void log(
            String feature,
            String action,
            Runnable logAction
    ) {
        try {
            MDC.put(
                    FEATURE_KEY,
                    feature
            );
            MDC.put(
                    ACTION_KEY,
                    action
            );

            logAction.run();
        } finally {
            MDC.remove(FEATURE_KEY);
            MDC.remove(ACTION_KEY);
        }
    }

    private String format(
            String message,
            Object... args
    ) {
        return MessageFormatter.arrayFormat(
                        message,
                        args
                )
                .getMessage();
    }

}