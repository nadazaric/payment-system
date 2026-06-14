package com.sep.bank.back.feature_payment.page;

import com.sep.bank.back.feature_payment.dto.PaymentPageDTO;
import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_qr.dto.QrPaymentPageContentDTO;
import com.sep.bank.back.feature_qr.service.interf.QrPaymentPageContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class PaymentPageRenderer {

    private static final String PAYMENT_EXPIRED_PAGE = "classpath:payment-pages/payment-expired-page.html";
    private static final String PAYMENT_USED_PAGE = "classpath:payment-pages/payment-used-page.html";
    private static final String CARD_PAYMENT_PAGE = "classpath:payment-pages/card-payment-page.html";
    private static final String QR_PAYMENT_PAGE = "classpath:payment-pages/qr-payment-page.html";
    private static final String PAYMENT_NOT_FOUND_PAGE = "classpath:payment-pages/payment-not-found-page.html";

    private final ResourceLoader resourceLoader;

    @Autowired
    QrPaymentPageContentService qrPaymentPageContentService;

    public PaymentPageRenderer(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String renderPaymentPage(PaymentPageDTO payment) {
        if (Boolean.TRUE.equals(payment.expired())) {
            return loadTemplate(PAYMENT_EXPIRED_PAGE);
        }

        if (Boolean.TRUE.equals(payment.paymentAttemptUsed()) || payment.status() != PaymentStatus.CREATED) {
            return loadTemplate(PAYMENT_USED_PAGE);
        }

        String template = loadTemplate(getPaymentPageTemplatePath(payment.paymentMethod()))
                .replace("{{amount}}", payment.amount().toString())
                .replace("{{currency}}", payment.currency());

        if (PaymentMethod.QR.equals(payment.paymentMethod())) {
            return renderQrPaymentPage(template, payment);
        }

        return template.replace("{{paymentFormContent}}", buildPaymentFormContent(payment));
    }

    public String renderNotFoundPage() {
        return loadTemplate(PAYMENT_NOT_FOUND_PAGE);
    }

    private String getPaymentPageTemplatePath(PaymentMethod paymentMethod) {
        if (PaymentMethod.QR.equals(paymentMethod)) {
            return QR_PAYMENT_PAGE;
        }

        return CARD_PAYMENT_PAGE;
    }

    private String loadTemplate(String path) {
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

    private String renderQrPaymentPage(
            String template,
            PaymentPageDTO payment
    ) {
        QrPaymentPageContentDTO qrContent = qrPaymentPageContentService.buildContent(
                payment.paymentId()
        );

        return template.replace(
                "{{qrImageBase64}}",
                qrContent.qrImageBase64()
        );
    }

    private String buildPaymentFormContent(PaymentPageDTO payment) {
        return """
            <form method="post" action="/api/bank/payments/%s/submit">
                <div class="form-grid">
                    <div class="field">
                        <label for="pan">Card number (PAN)</label>
                        <input
                            type="text"
                            id="pan"
                            name="pan"
                            inputmode="numeric"
                            autocomplete="cc-number"
                            maxlength="19"
                            placeholder="0000 0000 0000 0000"
                            required>
                        <span id="panError" class="error-text">Enter a valid 16-digit card number.</span>
                    </div>
            
                    <div class="field">
                        <label for="cardHolderName">Card holder name</label>
                        <input
                            type="text"
                            id="cardHolderName"
                            name="cardHolderName"
                            autocomplete="cc-name"
                            maxlength="100"
                            placeholder="Name printed on card"
                            required>
                        <span id="cardHolderNameError" class="error-text">Enter card holder name.</span>
                    </div>
            
                    <div class="form-row">
                        <div class="field">
                            <label for="expirationDate">Expiration date</label>
                            <input
                                type="text"
                                id="expirationDate"
                                name="expirationDate"
                                autocomplete="cc-exp"
                                maxlength="5"
                                placeholder="MM/YY"
                                required>
                            <span id="expirationDateError" class="error-text">Enter a valid future date in MM/YY format.</span>
                        </div>
            
                        <div class="field">
                            <label for="securityCode">Security code</label>
                            <input
                                type="password"
                                id="securityCode"
                                name="securityCode"
                                inputmode="numeric"
                                autocomplete="cc-csc"
                                maxlength="3"
                                placeholder="CVV"
                                required>
                            <span id="securityCodeError" class="error-text">Enter a valid 3-digit security code.</span>
                        </div>
                    </div>
                </div>
            
                <div class="actions">
                    <button
                        id="submitPaymentButton"
                        class="button"
                        type="submit"
                        disabled>
                        Submit payment
                    </button>
                </div>
            </form>
            """.formatted(payment.paymentId());
    }

}