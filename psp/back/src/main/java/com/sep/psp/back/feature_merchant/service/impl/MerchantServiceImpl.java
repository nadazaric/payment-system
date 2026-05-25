package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.mapper.MerchantMapper;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantCredentialGenerator;
import com.sep.psp.back.feature_merchant.service.interf.MerchantService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl implements MerchantService {

    private static final String DEFAULT_SELLER_REFERENCE = "MAIN_SELLER";
    private static final String DEFAULT_SELLER_DISPLAY_NAME = "Main seller";

    @Autowired MerchantRepository merchantRepository;

    @Autowired MerchantAdminRepository merchantAdminRepository;

    @Autowired MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired MerchantCredentialGenerator merchantCredentialGenerator;

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        validateRegistrationRequest(request);

        String merchantId = generateUniqueMerchantId();
        String merchantPassword = merchantCredentialGenerator.generateMerchantPassword();

        Merchant merchant = createMerchant(request, merchantId, merchantPassword);
        Merchant savedMerchant = merchantRepository.save(merchant);

        MerchantAdmin merchantAdmin = createMerchantAdmin(request, savedMerchant);
        MerchantAdmin savedMerchantAdmin = merchantAdminRepository.save(merchantAdmin);

        MerchantSellerAccount defaultSellerAccount = createDefaultSellerAccount(savedMerchant);
        MerchantSellerAccount savedDefaultSellerAccount = merchantSellerAccountRepository.save(defaultSellerAccount);

        return merchantMapper.toRegistrationResponse(
                savedMerchant,
                savedMerchantAdmin,
                savedDefaultSellerAccount,
                merchantPassword
        );
    }

    private void validateRegistrationRequest(MerchantRegistrationRequest request) {
        if (merchantAdminRepository.existsByUsername(request.adminUsername())) {
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

}