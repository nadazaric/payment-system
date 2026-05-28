package com.sep.psp.back.shared.logging.service.impl;

import com.sep.psp.back.shared.logging.LogContext;
import com.sep.psp.back.shared.logging.SensitiveDataSanitizer;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class AppLoggerServiceImpl implements AppLoggerService {

    @Override
    public void info(
            String feature,
            String action,
            String message,
            Object... arguments
    ) {
        try (LogContext ignored = LogContext.of(feature, action)) {
            log.info(
                    SensitiveDataSanitizer.sanitize(message),
                    sanitizeArguments(arguments)
            );
        }
    }

    @Override
    public void warn(
            String feature,
            String action,
            String message,
            Object... arguments
    ) {
        try (LogContext ignored = LogContext.of(feature, action)) {
            log.warn(
                    SensitiveDataSanitizer.sanitize(message),
                    sanitizeArguments(arguments)
            );
        }
    }

    @Override
    public void error(
            String feature,
            String action,
            String message,
            Object... arguments
    ) {
        try (LogContext ignored = LogContext.of(feature, action)) {
            log.error(
                    SensitiveDataSanitizer.sanitize(message),
                    sanitizeArguments(arguments)
            );
        }
    }

    private Object[] sanitizeArguments(Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return new Object[0];
        }

        return Arrays.stream(arguments)
                .map(this::sanitizeArgument)
                .toArray();
    }

    private Object sanitizeArgument(Object argument) {
        if (argument instanceof String stringArgument) {
            return SensitiveDataSanitizer.sanitize(stringArgument);
        }

        return argument;
    }
}