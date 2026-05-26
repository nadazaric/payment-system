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
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.security.jwt.JwtTokenUtil;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
    PaymentMethodRepository paymentMethodRepository;

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

    @Override
    @Transactional
    public MerchantLoginResponse loginMerchantAdmin(MerchantLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Invalid username or password."));

        String token = jwtTokenUtil.generateToken(
                merchantAdmin.getUsername(),
                "MERCHANT_ADMIN",
                merchantAdmin.getMerchant().getMerchantId()
        );

        return new MerchantLoginResponse(token);
    }

    @Override
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

        if (merchantSellerAccountRepository.existsByMerchantAndSellerReference(
                merchant,
                request.sellerReference()
        )) {
            throw new BadRequestException("Seller reference is already in use for this merchant.");
        }

        MerchantSellerAccount sellerAccount = new MerchantSellerAccount(
                merchant,
                request.sellerReference(),
                request.displayName()
        );

        MerchantSellerAccount savedSellerAccount = merchantSellerAccountRepository.save(sellerAccount);

        return merchantMapper.toSellerAccountResponse(savedSellerAccount);
    }

    @Override
    @Transactional
    public void updateSellerPaymentMethods(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request
    ) {
        if (request.paymentMethodCodes() == null || request.paymentMethodCodes().isEmpty()) {
            throw new BadRequestException("At least one payment method must be selected.");
        }

        String username = getAuthenticatedUsername();

        MerchantAdmin merchantAdmin = merchantAdminRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));

        MerchantSellerAccount sellerAccount = merchantSellerAccountRepository.findById(sellerId)
                .orElseThrow(() -> new BadRequestException("Seller account not found."));

        Merchant merchant = merchantAdmin.getMerchant();

        if (!sellerAccount.getMerchant().getMerchantId().equals(merchant.getMerchantId())) {
            throw new BadRequestException("Seller account does not belong to the authenticated merchant.");
        }

        Set<String> uniquePaymentMethodCodes = new LinkedHashSet<>(request.paymentMethodCodes());

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAllById(uniquePaymentMethodCodes);

        if (paymentMethods.size() != uniquePaymentMethodCodes.size()) {
            throw new BadRequestException("One or more payment methods do not exist.");
        }

        boolean hasInactivePaymentMethod = paymentMethods.stream()
                .anyMatch(paymentMethod -> !paymentMethod.isActive());

        if (hasInactivePaymentMethod) {
            throw new BadRequestException("One or more payment methods are not active.");
        }

        sellerAccount.getAvailablePaymentMethods().clear();
        sellerAccount.getAvailablePaymentMethods().addAll(paymentMethods);
        sellerAccount.setActive(!sellerAccount.getAvailablePaymentMethods().isEmpty());

        merchantSellerAccountRepository.save(sellerAccount);

        updateMerchantActiveStatus(merchant);
    }

    private void updateMerchantActiveStatus(Merchant merchant) {
        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(merchant);

        boolean hasActiveSeller = sellerAccounts.stream()
                .anyMatch(MerchantSellerAccount::isActive);

        merchant.setActive(hasActiveSeller);
        merchantRepository.save(merchant);
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
                .orElseThrow(() -> new BadRequestException("Seller account not found."));

        if (!sellerAccount.getMerchant().getMerchantId().equals(merchant.getMerchantId())) {
            throw new BadRequestException("Seller account does not belong to the authenticated merchant.");
        }

        boolean sellerReferenceChanged = !sellerAccount.getSellerReference().equals(request.sellerReference());

        if (
                sellerReferenceChanged
                        && merchantSellerAccountRepository.existsByMerchantAndSellerReference(
                        merchant,
                        request.sellerReference()
                )
        ) {
            throw new BadRequestException("Seller reference is already in use for this merchant.");
        }

        sellerAccount.setSellerReference(request.sellerReference());
        sellerAccount.setDisplayName(request.displayName());

        merchantSellerAccountRepository.save(sellerAccount);
    }

}