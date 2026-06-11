package com.sep.web_shop.back.shared.logging.service.interf;

public interface AppLoggerService {

    void info(
            String feature,
            String action,
            String message,
            Object... args
    );

    void warn(
            String feature,
            String action,
            String message,
            Object... args
    );

    void error(
            String feature,
            String action,
            String message,
            Object... args
    );

}