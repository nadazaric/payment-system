package com.sep.bank.plugin.back.feature_psp.controller;

import com.sep.bank.plugin.back.feature_psp.security.PspSecurityHeaders;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plugin")
public class HeartbeatController {

    @Autowired
    AppLoggerService appLoggerService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(
            @RequestHeader(PspSecurityHeaders.PLUGIN_CODE) String pluginCode
    ) {
        appLoggerService.info(
                LogStrings.Feature.APP,
                LogStrings.Action.HEARTBEAT,
                "pluginCode={} status=UP",
                pluginCode
        );

        return ResponseEntity.noContent()
                .build();
    }

}