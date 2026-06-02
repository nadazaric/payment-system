package com.sep.psp.back.feature_auth.controller;

import com.sep.psp.back.feature_auth.dto.LoginRequest;
import com.sep.psp.back.feature_auth.dto.LoginResponse;
import com.sep.psp.back.feature_auth.service.interf.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication."
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Operation(
            summary = "Login user",
            description = "Authenticates merchant admin or super admin and returns JWT token with role."
    )
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

}