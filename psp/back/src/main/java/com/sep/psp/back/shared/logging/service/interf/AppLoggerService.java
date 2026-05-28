package com.sep.psp.back.shared.logging.service.interf;

public interface AppLoggerService {

    void info(String feature, String action, String message, Object... arguments);

    void warn(String feature, String action, String message, Object... arguments);

    void error(String feature, String action, String message, Object... arguments);

}