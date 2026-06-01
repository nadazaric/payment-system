package com.sep.psp.back.feature_merchant.repository;

import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantSellerPaymentMethodRepository extends JpaRepository<MerchantSellerPaymentMethod, String> {

    List<MerchantSellerPaymentMethod> findBySellerAccount(MerchantSellerAccount sellerAccount);

    Optional<MerchantSellerPaymentMethod> findBySellerAccountAndPaymentMethod(
            MerchantSellerAccount sellerAccount,
            PaymentMethod paymentMethod
    );

    List<MerchantSellerPaymentMethod> findByPaymentMethod(PaymentMethod paymentMethod);

}