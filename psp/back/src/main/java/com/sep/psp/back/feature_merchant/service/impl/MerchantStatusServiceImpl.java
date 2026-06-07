package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantStatusService;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantStatusServiceImpl implements MerchantStatusService {

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public void refreshSellerAndMerchantStatus(MerchantSellerAccount sellerAccount) {
        refreshSellersAndMerchants(List.of(sellerAccount));
    }

    @Override
    @Transactional
    public void refreshStatusesForSellerPaymentMethods(
            List<MerchantSellerPaymentMethod> sellerPaymentMethods
    ) {
        Map<String, MerchantSellerAccount> affectedSellers = new LinkedHashMap<>();

        sellerPaymentMethods.forEach(sellerPaymentMethod -> affectedSellers.put(
                sellerPaymentMethod.getSellerAccount().getId(),
                sellerPaymentMethod.getSellerAccount()
        ));

        refreshSellersAndMerchants(affectedSellers.values());
    }

    @Override
    @Transactional
    public void refreshStatusesForPaymentMethod(PaymentMethod paymentMethod) {
        List<MerchantSellerPaymentMethod> sellerPaymentMethods =
                merchantSellerPaymentMethodRepository.findByPaymentMethod(paymentMethod);

        refreshStatusesForSellerPaymentMethods(sellerPaymentMethods);
    }

    @Override
    @Transactional
    public void refreshStatusesForPlugin(PaymentPlugin paymentPlugin) {
        List<PaymentMethod> pluginPaymentMethods = paymentMethodRepository.findByPlugin(paymentPlugin);

        Map<String, MerchantSellerPaymentMethod> affectedSellerPaymentMethods = new LinkedHashMap<>();

        pluginPaymentMethods.forEach(paymentMethod -> {
            List<MerchantSellerPaymentMethod> sellerPaymentMethods =
                    merchantSellerPaymentMethodRepository.findByPaymentMethod(paymentMethod);

            sellerPaymentMethods.forEach(sellerPaymentMethod -> affectedSellerPaymentMethods.put(
                    sellerPaymentMethod.getId(),
                    sellerPaymentMethod
            ));
        });

        refreshStatusesForSellerPaymentMethods(
                affectedSellerPaymentMethods.values()
                        .stream()
                        .toList()
        );
    }

    private void refreshSellersAndMerchants(Collection<MerchantSellerAccount> sellerAccounts) {
        Map<String, Merchant> affectedMerchants = new LinkedHashMap<>();

        sellerAccounts.forEach(sellerAccount -> {
            updateSellerActiveStatus(sellerAccount);

            affectedMerchants.put(
                    sellerAccount.getMerchant().getMerchantId(),
                    sellerAccount.getMerchant()
            );
        });

        affectedMerchants.values().forEach(this::updateMerchantActiveStatus);
    }

    private void updateSellerActiveStatus(MerchantSellerAccount sellerAccount) {
        boolean sellerActive = merchantSellerPaymentMethodRepository.findBySellerAccount(sellerAccount)
                .stream()
                .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);

        sellerAccount.setActive(sellerActive);

        merchantSellerAccountRepository.saveAndFlush(sellerAccount);
    }

    private void updateMerchantActiveStatus(Merchant merchant) {
        boolean previousMerchantActive = merchant.isActive();

        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(merchant);

        boolean merchantActive = sellerAccounts.stream()
                .anyMatch(MerchantSellerAccount::isActive);

        merchant.setActive(merchantActive);

        merchantRepository.saveAndFlush(merchant);

        if (previousMerchantActive != merchant.isActive()) {
            appLoggerService.info(
                    LogStrings.Feature.MERCHANT,
                    LogStrings.Action.ACTIVE_STATUS_CHANGED,
                    "merchantId={} active={}",
                    merchant.getMerchantId(),
                    merchant.isActive()
            );
        }
    }

}