package com.sep.psp.back.feature_merchant.controller;

import com.sep.psp.back.feature_merchant.dto.*;
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

import java.util.List;

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

    // ----------------------------------------------------------------------------------------------------------------- Get Profile
    @Operation(
            summary = "Get current merchant profile",
            description = """
                Returns profile information for the currently authenticated merchant admin.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Merchant profile returned successfully.",
                    content = @Content(schema = @Schema(implementation = MerchantProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid JWT token.",
                    content = @Content
            )
    })
    @GetMapping("/profile")
    public MerchantProfileResponse getCurrentMerchantProfile() {
        return merchantService.getCurrentMerchantProfile();
    }

    // ----------------------------------------------------------------------------------------------------------------- Get Sellers
    @Operation(
            summary = "Get merchant seller accounts",
            description = """
                Returns seller accounts that belong to the currently authenticated merchant admin.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Seller accounts returned successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid JWT token.",
                    content = @Content
            )
    })
    @GetMapping("/sellers")
    public List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts() {
        return merchantService.getCurrentMerchantSellerAccounts();
    }

    // ----------------------------------------------------------------------------------------------------------------- Create Seller
    @Operation(
            summary = "Create merchant seller account",
            description = """
                Creates a new seller account for the currently authenticated merchant.
                The seller account is created as inactive until payment methods are configured.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Seller account created successfully.",
                    content = @Content(schema = @Schema(implementation = MerchantSellerAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or seller reference already exists.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid JWT token.",
                    content = @Content
            )
    })
    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public MerchantSellerAccountResponse createSellerAccount(
            @Valid @RequestBody CreateMerchantSellerAccountRequest request
    ) {
        return merchantService.createSellerAccount(request);
    }

}