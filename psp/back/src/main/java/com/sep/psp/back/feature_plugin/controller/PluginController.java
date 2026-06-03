package com.sep.psp.back.feature_plugin.controller;

import com.sep.psp.back.feature_plugin.dto.PluginSyncResponse;
import com.sep.psp.back.feature_plugin.service.interf.PluginSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Payment plugins",
        description = "Endpoints for synchronizing payment plugins."
)
@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    @Autowired
    PluginSyncService pluginSyncService;

    @Operation(
            summary = "Synchronize payment plugin",
            description = """
                    Synchronizes an expected payment plugin and its payment methods.
                    Plugin must already be created by PSP super admin and must sign the request.
                    """
    )
    @PostMapping("/sync")
    public PluginSyncResponse syncPlugin(
            @RequestHeader("X-Plugin-Code") String pluginCodeHeader,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Signature") String signature,
            @RequestBody String requestBody
    ) {
        return pluginSyncService.syncPlugin(
                pluginCodeHeader,
                timestamp,
                signature,
                requestBody
        );
    }

}