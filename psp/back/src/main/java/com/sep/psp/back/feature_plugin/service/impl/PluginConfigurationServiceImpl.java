package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationResponse;
import com.sep.psp.back.feature_plugin.service.interf.PluginConfigurationService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PluginConfigurationServiceImpl implements PluginConfigurationService {

    private static final String CONFIGURATION_ENDPOINT = "/api/plugin/configurations";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PluginConfigurationResponse configurePaymentMethod(
            PaymentMethod paymentMethod,
            Merchant merchant,
            MerchantSellerAccount sellerAccount,
            Map<String, String> values
    ) {
        String configurationUrl = buildConfigurationUrl(paymentMethod);

        PluginConfigurationRequest request = new PluginConfigurationRequest(
                merchant.getMerchantId(),
                sellerAccount.getSellerReference(),
                paymentMethod.getCode(),
                values
        );

        try {
            ResponseEntity<PluginConfigurationResponse> response = restTemplate.postForEntity(
                    configurationUrl,
                    request,
                    PluginConfigurationResponse.class
            );

            PluginConfigurationResponse responseBody = response.getBody();

            if (responseBody == null) {
                throw new BadRequestException("Payment plugin returned empty configuration response.");
            }

            return responseBody;
        } catch (RestClientException exception) {
            throw new BadRequestException("Payment plugin configuration request failed.");
        }
    }

    private String buildConfigurationUrl(PaymentMethod paymentMethod) {
        String baseUrl = paymentMethod.getPlugin().getBaseUrl();

        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + CONFIGURATION_ENDPOINT;
        }

        return baseUrl + CONFIGURATION_ENDPOINT;
    }
}