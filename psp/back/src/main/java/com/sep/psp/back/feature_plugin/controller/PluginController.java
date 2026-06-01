package com.sep.psp.back.feature_plugin.controller;

import com.sep.psp.back.feature_plugin.dto.PluginRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginRegistrationResponse;
import com.sep.psp.back.feature_plugin.service.interf.PluginRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Plugins",
        description = "Endpoints for registering payment plugins."
)
@RestController
@RequestMapping("/api/plugins")
public class PluginController {

    @Autowired
    PluginRegistryService pluginRegistryService;

    @Operation(
            summary = "Register payment plugin",
            description = """
                    Registers or updates a payment plugin and its payment methods.
                    The plugin sends its full manifest when it starts.
                    """
    )
    @PostMapping("/register")
    public PluginRegistrationResponse registerPlugin(
            @Valid @RequestBody PluginRegistrationRequest request
    ) {
        return pluginRegistryService.registerPlugin(request);
    }

}