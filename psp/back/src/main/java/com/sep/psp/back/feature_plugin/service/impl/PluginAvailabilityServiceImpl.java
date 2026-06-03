package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PluginAvailabilityServiceImpl implements PluginAvailabilityService {

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Override
    @Transactional
    public void markPluginActive(PaymentPlugin paymentPlugin) {
        if (paymentPlugin.isActive()) {
            return;
        }

        paymentPlugin.setActive(true);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        updateAffectedSellerAndMerchantStatuses(savedPlugin);
    }

    @Override
    @Transactional
    public void markPluginInactive(PaymentPlugin paymentPlugin) {
        if (!paymentPlugin.isActive()) {
            return;
        }

        paymentPlugin.setActive(false);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        updateAffectedSellerAndMerchantStatuses(savedPlugin);
    }

    private void updateAffectedSellerAndMerchantStatuses(PaymentPlugin paymentPlugin) {
        List<PaymentMethod> pluginPaymentMethods = paymentMethodRepository.findByPlugin(paymentPlugin);

        Map<String, MerchantSellerAccount> affectedSellers = new LinkedHashMap<>();

        pluginPaymentMethods.forEach(paymentMethod -> {
            List<MerchantSellerPaymentMethod> sellerPaymentMethods =
                    merchantSellerPaymentMethodRepository.findByPaymentMethod(paymentMethod);

            sellerPaymentMethods.forEach(sellerPaymentMethod -> affectedSellers.put(
                    sellerPaymentMethod.getSellerAccount().getId(),
                    sellerPaymentMethod.getSellerAccount()
            ));
        });

        affectedSellers.values().forEach(this::updateSellerActiveStatus);

        Map<String, Merchant> affectedMerchants = new LinkedHashMap<>();

        affectedSellers.values().forEach(sellerAccount -> affectedMerchants.put(
                sellerAccount.getMerchant().getMerchantId(),
                sellerAccount.getMerchant()
        ));

        affectedMerchants.values().forEach(this::updateMerchantActiveStatus);
    }

    private void updateSellerActiveStatus(MerchantSellerAccount sellerAccount) {
        boolean sellerActive = merchantSellerPaymentMethodRepository.findBySellerAccount(sellerAccount)
                .stream()
                .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);

        sellerAccount.setActive(sellerActive);

        merchantSellerAccountRepository.save(sellerAccount);
    }

    private void updateMerchantActiveStatus(Merchant merchant) {
        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(merchant);

        boolean merchantActive = sellerAccounts.stream()
                .anyMatch(MerchantSellerAccount::isActive);

        merchant.setActive(merchantActive);

        merchantRepository.save(merchant);
    }

}