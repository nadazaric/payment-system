package com.sep.bank.plugin.back.feature_psp.controller;

import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationRequest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationResponse;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.security.SignatureHeaders;
import com.sep.bank.plugin.back.feature_psp.service.interf.PluginConfigurationService;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plugin")
public class PspController {

    @Autowired
    PluginConfigurationService pluginConfigurationService;

    @Autowired
    AppLoggerService appLoggerService;

    @PostMapping("/heartbeat")
    @Operation(summary = "Heartbeat check")
    public ResponseEntity<Void> heartbeat(@RequestHeader(SignatureHeaders.PLUGIN_CODE) String pluginCode) {
//        appLoggerService.info(
//                LogStrings.Feature.APP,
//                LogStrings.Action.HEARTBEAT,
//                "pluginCode={} status=UP",
//                pluginCode
//        );

        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/configurations")
    @Operation(
            summary = "Configure seller payment method",
            description = "Stores bank merchant ID for merchant, seller and payment method."
    )
    public PluginConfigurationResponse configure(@Valid @RequestBody PluginConfigurationRequest request) {
        return pluginConfigurationService.configure(request);
    }

}