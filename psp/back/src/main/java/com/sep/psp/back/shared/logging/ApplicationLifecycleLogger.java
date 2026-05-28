package com.sep.psp.back.shared.logging;

import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLifecycleLogger {

    @Autowired
    AppLoggerService appLoggerService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        appLoggerService.info(
                LogStrings.Feature.APP,
                LogStrings.Action.STARTED,
                "=============== PSP Core application started ==============="
        );
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationStopped() {
        appLoggerService.info(
                LogStrings.Feature.APP,
                LogStrings.Action.STOPPED,
                "=============== PSP Core application stopped ==============="
        );
    }
}