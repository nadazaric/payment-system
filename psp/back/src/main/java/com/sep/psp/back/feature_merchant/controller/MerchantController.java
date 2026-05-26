package com.sep.psp.back.feature_merchant.controller;

import com.sep.psp.back.feature_merchant.dto.MerchantLoginRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantLoginResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.service.interf.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Merchant",
        description = "Endpoints for merchant shop onboarding and configuration."
)
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    MerchantService merchantService;

    // ----------------------------------------------------------------------------------------------------------------- Register
    @Operation(
            summary = "Register a new merchant shop",
            description = """
                    Registers a new merchant shop on PSP.
                    The PSP creates a merchant account, merchant admin account and default seller account.
                    The generated merchant password/API key is returned only once and must be stored by the merchant.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Merchant registered successfully.",
                    content = @Content(schema = @Schema(implementation = MerchantRegistrationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or merchant admin username already exists.",
                    content = @Content
            )
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantRegistrationResponse registerMerchant(
            @Valid @RequestBody MerchantRegistrationRequest request
    ) {
        return merchantService.registerMerchant(request);
    }

    // ----------------------------------------------------------------------------------------------------------------- Login
    @Operation(
            summary = "Log in merchant admin",
            description = """
                Authenticates a merchant admin using username and password.
                Returns a JWT token that must be used as Bearer token for protected merchant endpoints.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Merchant admin logged in successfully.",
                    content = @Content(schema = @Schema(implementation = MerchantLoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password.",
                    content = @Content
            )
    })
    @PostMapping("/login")
    public MerchantLoginResponse loginMerchantAdmin(
            @Valid @RequestBody MerchantLoginRequest request
    ) {
        return merchantService.loginMerchantAdmin(request);
    }

}