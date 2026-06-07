package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.dto.CreateMerchantSellerAccountRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantSellerAccountResponse;
import com.sep.psp.back.feature_merchant.dto.UpdateMerchantSellerAccountRequest;
import com.sep.psp.back.feature_merchant.mapper.MerchantMapper;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantAdminContextService;
import com.sep.psp.back.feature_merchant.service.interf.MerchantSellerService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MerchantSellerServiceImpl implements MerchantSellerService {

    private static final String DEFAULT_SELLER_REFERENCE = "MAIN_SELLER";
    private static final String DEFAULT_SELLER_DISPLAY_NAME = "Main seller";

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    MerchantAdminContextService merchantAdminContextService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public MerchantSellerAccount createDefaultSellerAccount(Merchant merchant) {
        return new MerchantSellerAccount(
                merchant,
                DEFAULT_SELLER_REFERENCE,
                DEFAULT_SELLER_DISPLAY_NAME
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts() {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(
                merchantAdmin.getMerchant()
        );

        return merchantMapper.toSellerAccountResponseList(sellerAccounts);
    }

    @Override
    @Transactional
    public MerchantSellerAccountResponse createSellerAccount(CreateMerchantSellerAccountRequest request) {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

        Merchant merchant = merchantAdmin.getMerchant();

        if (merchantSellerAccountRepository.existsByMerchantAndSellerReference(
                merchant,
                request.sellerReference()
        )) {
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
    public void updateSellerAccount(String sellerId, UpdateMerchantSellerAccountRequest request) {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getAuthenticatedMerchantAdmin();

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

        boolean sellerReferenceChanged = !sellerAccount.getSellerReference().equals(request.sellerReference());

        if (sellerReferenceChanged && merchantSellerAccountRepository.existsByMerchantAndSellerReference(
                merchant,
                request.sellerReference()
        )) {
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

}