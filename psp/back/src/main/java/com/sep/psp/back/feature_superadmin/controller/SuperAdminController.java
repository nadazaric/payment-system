package com.sep.psp.back.feature_superadmin.controller;

import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginRequest;
import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginResponse;
import com.sep.psp.back.feature_superadmin.dto.SuperAdminPluginResponse;
import com.sep.psp.back.feature_superadmin.service.interf.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "PSP Super Admin",
        description = "Endpoints for PSP super admin."
)
@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasAuthority(T(com.sep.psp.back.feature_auth.enumeration.UserRole).SUPER_ADMIN.authority())")
public class SuperAdminController {

    @Autowired
    SuperAdminService superAdminService;

    @Operation(
            summary = "Create expected payment plugin",
            description = "Creates an expected payment plugin and returns generated plugin secret."
    )
    @PostMapping("/plugins")
    public CreateExpectedPluginResponse createExpectedPlugin(
            @Valid @RequestBody CreateExpectedPluginRequest request
    ) {
        return superAdminService.createExpectedPlugin(request);
    }

    @Operation(
            summary = "Get payment plugins",
            description = "Returns payment plugins visible to PSP super admin."
    )
    @GetMapping("/plugins")
    public List<SuperAdminPluginResponse> getPlugins() {
        return superAdminService.getPlugins();
    }

}