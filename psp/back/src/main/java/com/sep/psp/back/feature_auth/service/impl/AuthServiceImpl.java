package com.sep.psp.back.feature_auth.service.impl;

import com.sep.psp.back.feature_auth.dto.LoginRequest;
import com.sep.psp.back.feature_auth.dto.LoginResponse;
import com.sep.psp.back.feature_auth.enumeration.UserRole;
import com.sep.psp.back.feature_auth.service.interf.AuthService;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_superadmin.model.SuperAdmin;
import com.sep.psp.back.feature_superadmin.repository.SuperAdminRepository;
import com.sep.psp.back.security.jwt.JwtTokenUtil;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String PSP_ISSUER = "PSP";

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SuperAdminRepository superAdminRepository;

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (AuthenticationException exception) {
            appLoggerService.warn(
                    LogStrings.Feature.AUTH,
                    LogStrings.Action.LOGIN_REJECTED,
                    "username={}",
                    request.username()
            );

            throw exception;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return superAdminRepository.findByUsername(request.username())
                .map(this::createSuperAdminLoginResponse)
                .orElseGet(() -> createMerchantAdminLoginResponse(request.username()));
    }

    private LoginResponse createSuperAdminLoginResponse(SuperAdmin superAdmin) {
        if (!superAdmin.isActive()) {
            throw new BadRequestException("Super admin account is not active.");
        }

        String token = jwtTokenUtil.generateToken(
                superAdmin.getUsername(),
                UserRole.SUPER_ADMIN.name(),
                PSP_ISSUER
        );

        appLoggerService.info(
                LogStrings.Feature.AUTH,
                LogStrings.Action.LOGIN_SUCCESS,
                "username={} role={}",
                superAdmin.getUsername(),
                UserRole.SUPER_ADMIN.name()
        );

        return new LoginResponse(
                token,
                UserRole.SUPER_ADMIN.name()
        );
    }

    private LoginResponse createMerchantAdminLoginResponse(String username) {
        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid username or password."));

        if (!merchantAdmin.isActive()) {
            throw new BadRequestException("Merchant admin account is not active.");
        }

        String token = jwtTokenUtil.generateToken(
                merchantAdmin.getUsername(),
                UserRole.MERCHANT_ADMIN.name(),
                merchantAdmin.getMerchant().getMerchantId()
        );

        appLoggerService.info(
                LogStrings.Feature.AUTH,
                LogStrings.Action.LOGIN_SUCCESS,
                "username={} merchantId={} role={}",
                merchantAdmin.getUsername(),
                merchantAdmin.getMerchant().getMerchantId(),
                UserRole.MERCHANT_ADMIN.name()
        );

        return new LoginResponse(
                token,
                UserRole.MERCHANT_ADMIN.name()
        );
    }

}