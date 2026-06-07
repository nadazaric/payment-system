package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.dto.*;
import com.sep.psp.back.feature_merchant.mapper.MerchantMapper;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantAdminContextService;
import com.sep.psp.back.feature_merchant.service.interf.MerchantSellerService;
import com.sep.psp.back.feature_merchant.service.interf.MerchantService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import com.sep.psp.back.shared.service.interf.ApiKeyGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    MerchantSellerService merchantSellerService;

    @Autowired
    MerchantAdminContextService merchantAdminContextService;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.merchant-id.prefix}")
    private String merchantIdPrefix;

    @Value("${app.merchant-id.alphabet}")
    private String merchantIdAlphabet;

    @Value("${app.merchant-id.length}")
    private int merchantIdLength;

    @Value("${app.merchant-password.prefix}")
    private String merchantPasswordPrefix;

    @Value("${app.merchant-password.alphabet}")
    private String merchantPasswordAlphabet;

    @Value("${app.merchant-password.length}")
    private int merchantPasswordLength;

    @Autowired
    ApiKeyGeneratorService apiKeyGeneratorService;

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
        String merchantPassword = apiKeyGeneratorService.generateApiKey(
                merchantPasswordPrefix,
                merchantPasswordAlphabet,
                merchantPasswordLength
        );

        Merchant merchant = createMerchant(request, merchantId, merchantPassword);
        Merchant savedMerchant = merchantRepository.save(merchant);

        MerchantAdmin merchantAdmin = createMerchantAdmin(request, savedMerchant);
        MerchantAdmin savedMerchantAdmin = merchantAdminRepository.save(merchantAdmin);

        MerchantSellerAccount defaultSellerAccount = merchantSellerService.createDefaultSellerAccount(savedMerchant);
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

    private String generateUniqueMerchantId() {
        String merchantId;

        do {
            merchantId = apiKeyGeneratorService.generateApiKey(
                    merchantIdPrefix,
                    merchantIdAlphabet,
                    merchantIdLength
            );
        } while (merchantRepository.existsById(merchantId));

        return merchantId;
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

    @Override
    @Transactional(readOnly = true)
    public MerchantProfileResponse getCurrentMerchantProfile() {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

        return merchantMapper.toProfileResponse(merchantAdmin);
    }

    @Override
    @Transactional
    public void updateCurrentMerchantProfile(UpdateMerchantProfileRequest request) {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

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
                merchantAdmin.getUsername()
        );
    }

    @Override
    @Transactional
    public RegenerateMerchantPasswordResponse regenerateMerchantPassword() {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

        String newMerchantPassword = apiKeyGeneratorService.generateApiKey(
                merchantPasswordPrefix,
                merchantPasswordAlphabet,
                merchantPasswordLength
        );
        String newMerchantPasswordHash = passwordEncoder.encode(newMerchantPassword);

        Merchant merchant = merchantAdmin.getMerchant();
        merchant.setMerchantPasswordHash(newMerchantPasswordHash);

        merchantRepository.save(merchant);

        appLoggerService.info(
                LogStrings.Feature.MERCHANT,
                LogStrings.Action.API_KEY_REGENERATED,
                "merchantId={} username={}",
                merchant.getMerchantId(),
                merchantAdmin.getUsername()
        );

        return new RegenerateMerchantPasswordResponse(newMerchantPassword);
    }

}