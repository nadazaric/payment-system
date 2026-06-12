package com.sep.bank.back.feature_payment.page;

import com.sep.bank.back.feature_payment.dto.PaymentPageDTO;
import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class PaymentPageRenderer {

    private static final String CARD_PAYMENT_PAGE = "classpath:payment-pages/card-payment-page.html";
    private static final String QR_PAYMENT_PAGE = "classpath:payment-pages/qr-payment-page.html";
    private static final String PAYMENT_NOT_FOUND_PAGE = "classpath:payment-pages/payment-not-found-page.html";

    private final ResourceLoader resourceLoader;

    public PaymentPageRenderer(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String renderPaymentPage(
            PaymentPageDTO payment
    ) {
        String template = loadTemplate(getPaymentPageTemplatePath(payment.paymentMethod()));

        return template
                .replace("{{paymentId}}", payment.paymentId().toString())
                .replace("{{amount}}", payment.amount().toString())
                .replace("{{currency}}", payment.currency())
                .replace("{{paymentMethod}}", payment.paymentMethod().name());
    }

    public String renderNotFoundPage() {
        return loadTemplate(PAYMENT_NOT_FOUND_PAGE);
    }

    private String getPaymentPageTemplatePath(
            PaymentMethod paymentMethod
    ) {
        if (PaymentMethod.QR.equals(paymentMethod)) {
            return QR_PAYMENT_PAGE;
        }

        return CARD_PAYMENT_PAGE;
    }

    private String loadTemplate(
            String path
    ) {
        try {
            Resource resource = resourceLoader.getResource(path);

            return StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Payment page template could not be loaded.");
        }
    }

}