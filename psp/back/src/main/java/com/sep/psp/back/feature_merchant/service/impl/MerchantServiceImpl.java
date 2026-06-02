package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.dto.*;
import com.sep.psp.back.feature_merchant.mapper.MerchantMapper;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantCredentialGenerator;
import com.sep.psp.back.feature_merchant.service.interf.MerchantService;
import com.sep.psp.back.feature_merchant.service.interf.SellerPaymentMethodService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MerchantServiceImpl implements MerchantService {

    private static final String DEFAULT_SELLER_REFERENCE = "MAIN_SELLER";
    private static final String DEFAULT_SELLER_DISPLAY_NAME = "Main seller";

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantCredentialGenerator merchantCredentialGenerator;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SellerPaymentMethodService sellerPaymentMethodService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        appLoggerService.info(
                LogStrings.Feature.MERCHANT,
                LogStrings.Action.REGISTER_STARTED,
                "adminUsername={} merchantName={}",
                request.adminUsername(),
                request.merchantName()
        );

        validateRegistrationRequest(request);

        String merchantId = generateUniqueMerchantId();
        String merchantPassword = merchantCredentialGenerator.generateMerchantPassword();

        Merchant merchant = createMerchant(request, merchantId, merchantPassword);
        Merchant savedMerchant = merchantRepository.save(merchant);

        MerchantAdmin merchantAdmin = createMerchantAdmin(request, savedMerchant);
        MerchantAdmin savedMerchantAdmin = merchantAdminRepository.save(merchantAdmin);

        MerchantSellerAccount defaultSellerAccount = createDefaultSellerAccount(savedMerchant);
        MerchantSellerAccount savedDefaultSellerAccount = merchantSellerAccountRepository.save(defaultSellerAccount);

        appLoggerService.info(
                LogStrings.Feature.MERCHANT,
                LogStrings.Action.REGISTER_COMPLETED,
                "merchantId={} adminUsername={} defaultSellerId={}",
                savedMerchant.getMerchantId(),
                savedMerchantAdmin.getUsername(),
                savedDefaultSellerAccount.getId()
        );

        return merchantMapper.toRegistrationResponse(
                savedMerchant,
                savedMerchantAdmin,
                savedDefaultSellerAccount,
                merchantPassword
        );
    }

    private void validateRegistrationRequest(MerchantRegistrationRequest request) {
        if (merchantAdminRepository.existsByUsername(request.adminUsername())) {
            appLoggerService.warn(
                    LogStrings.Feature.MERCHANT,
                    LogStrings.Action.REGISTER_REJECTED,
                    "reason={} adminUsername={}",
                    LogStrings.Reason.USERNAME_TAKEN,
                    request.adminUsername()
            );

            throw new BadRequestException("Merchant admin username is already in use.");
        }
    }

    private Merchant createMerchant(
            MerchantRegistrationRequest request,
            String merchantId,
            String merchantPassword
    ) {
        String merchantPasswordHash = passwordEncoder.encode(merchantPassword);

        return new Merchant(
                merchantId,
                request.merchantName(),
                merchantPasswordHash,
                request.currency().toUpperCase(),
                request.successUrl(),
                request.failUrl(),
                request.errorUrl()
        );
    }

    private MerchantAdmin createMerchantAdmin(
            MerchantRegistrationRequest request,
            Merchant merchant
    ) {
        String adminPasswordHash = passwordEncoder.encode(request.adminPassword());

        return new MerchantAdmin(
                merchant,
                request.adminUsername(),
                adminPasswordHash,
                request.adminName()
        );
    }

    private MerchantSellerAccount createDefaultSellerAccount(Merchant merchant) {
        return new MerchantSellerAccount(
                merchant,
                DEFAULT_SELLER_REFERENCE,
                DEFAULT_SELLER_DISPLAY_NAME
        );
    }

    private String generateUniqueMerchantId() {
        String merchantId;

        do {
            merchantId = merchantCredentialGenerator.generateMerchantId();
        } while (merchantRepository.existsById(merchantId));

        return merchantId;
    }

    @Override
    @Transactional(readOnly = true)
    public MerchantProfileResponse getCurrentMerchantProfile() {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        return merchantMapper.toProfileResponse(merchantAdmin);
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Authenticated user not found.");
        }

        return authentication.getName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts() {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(
                merchantAdmin.getMerchant()
        );

        return merchantMapper.toSellerAccountResponseList(sellerAccounts);
    }

    @Override
    @Transactional
    public MerchantSellerAccountResponse createSellerAccount(CreateMerchantSellerAccountRequest request) {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        Merchant merchant = merchantAdmin.getMerchant();

        if (merchantSellerAccountRepository.existsByMerchantAndSellerReference(merchant, request.sellerReference())) {
            appLoggerService.warn(
                    LogStrings.Feature.SELLER,
                    LogStrings.Action.SELLER_CREATE_REJECTED,
                    "reason={} merchantId={} sellerReference={}",
                    LogStrings.Reason.SELLER_REFERENCE_TAKEN,
                    merchant.getMerchantId(),
                    request.sellerReference()
            );

            throw new BadRequestException("Seller reference is already in use for this merchant.");
        }

        MerchantSellerAccount sellerAccount = new MerchantSellerAccount(
                merchant,
                request.sellerReference(),
                request.displayName()
        );

        MerchantSellerAccount savedSellerAccount = merchantSellerAccountRepository.save(sellerAccount);

        appLoggerService.info(
                LogStrings.Feature.SELLER,
                LogStrings.Action.SELLER_CREATED,
                "merchantId={} sellerId={} sellerReference={}",
                merchant.getMerchantId(),
                savedSellerAccount.getId(),
                savedSellerAccount.getSellerReference()
        );

        return merchantMapper.toSellerAccountResponse(savedSellerAccount);
    }

    @Override
    @Transactional
    public void updateSellerPaymentMethods(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request
    ) {
        sellerPaymentMethodService.updateSellerPaymentMethods(
                sellerId,
                request,
                getAuthenticatedUsername()
        );
    }

    @Override
    @Transactional
    public void updateCurrentMerchantProfile(UpdateMerchantProfileRequest request) {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        Merchant merchant = merchantAdmin.getMerchant();

        merchant.setMerchantName(request.merchantName());
        merchant.setCurrency(request.currency().toUpperCase());
        merchant.setSuccessUrl(request.successUrl());
        merchant.setFailUrl(request.failUrl());
        merchant.setErrorUrl(request.errorUrl());

        merchantRepository.save(merchant);

        appLoggerService.info(
                LogStrings.Feature.MERCHANT,
                LogStrings.Action.PROFILE_UPDATED,
                "merchantId={} username={}",
                merchant.getMerchantId(),
                username
        );
    }

    @Override
    @Transactional
    public RegenerateMerchantPasswordResponse regenerateMerchantPassword() {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        String newMerchantPassword = merchantCredentialGenerator.generateMerchantPassword();
        String newMerchantPasswordHash = passwordEncoder.encode(newMerchantPassword);

        Merchant merchant = merchantAdmin.getMerchant();
        merchant.setMerchantPasswordHash(newMerchantPasswordHash);

        merchantRepository.save(merchant);

        appLoggerService.info(
                LogStrings.Feature.MERCHANT,
                LogStrings.Action.API_KEY_REGENERATED,
                "merchantId={} username={}",
                merchant.getMerchantId(),
                username
        );

        return new RegenerateMerchantPasswordResponse(newMerchantPassword);
    }

    @Override
    @Transactional
    public void updateSellerAccount(
            String sellerId,
            UpdateMerchantSellerAccountRequest request
    ) {
        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        Merchant merchant = merchantAdmin.getMerchant();

        MerchantSellerAccount sellerAccount = merchantSellerAccountRepository.findById(sellerId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.SELLER,
                            LogStrings.Action.SELLER_UPDATE_REJECTED,
                            "reason={} merchantId={} sellerId={}",
                            LogStrings.Reason.SELLER_NOT_FOUND,
                            merchant.getMerchantId(),
                            sellerId
                    );

                    return new BadRequestException("Seller account not found.");
                });

        if (!sellerAccount.getMerchant().getMerchantId().equals(merchant.getMerchantId())) {
            appLoggerService.warn(
                    LogStrings.Feature.SELLER,
                    LogStrings.Action.SELLER_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={}",
                    LogStrings.Reason.OWNER_MISMATCH,
                    merchant.getMerchantId(),
                    sellerId
            );

            throw new BadRequestException("Seller account does not belong to the authenticated merchant.");
        }

        Boolean sellerReferenceChanged = !sellerAccount.getSellerReference().equals(request.sellerReference());

        if (sellerReferenceChanged && merchantSellerAccountRepository.existsByMerchantAndSellerReference(merchant, request.sellerReference())) {
            appLoggerService.warn(
                    LogStrings.Feature.SELLER,
                    LogStrings.Action.SELLER_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={} sellerReference={}",
                    LogStrings.Reason.SELLER_REFERENCE_TAKEN,
                    merchant.getMerchantId(),
                    sellerId,
                    request.sellerReference()
            );

            throw new BadRequestException("Seller reference is already in use for this merchant.");
        }

        sellerAccount.setSellerReference(request.sellerReference());
        sellerAccount.setDisplayName(request.displayName());

        merchantSellerAccountRepository.save(sellerAccount);

        appLoggerService.info(
                LogStrings.Feature.SELLER,
                LogStrings.Action.SELLER_UPDATED,
                "merchantId={} sellerId={} sellerReference={}",
                merchant.getMerchantId(),
                sellerAccount.getId(),
                sellerAccount.getSellerReference()
        );
    }

    @Override
    @Transactional
    public ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            ConfigureSellerPaymentMethodRequest request
    ) {
        return sellerPaymentMethodService.configureSellerPaymentMethod(
                sellerId,
                paymentMethodCode,
                request,
                getAuthenticatedUsername()
        );
    }

}