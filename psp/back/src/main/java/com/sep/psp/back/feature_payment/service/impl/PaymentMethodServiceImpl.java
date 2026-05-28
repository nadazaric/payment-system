package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;
import com.sep.psp.back.feature_payment.mapper.PaymentMethodMapper;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_payment.service.interf.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    PaymentMethodMapper paymentMethodMapper;

    @Override
    public List<PaymentMethodResponse> getActivePaymentMethods() {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByActiveTrue();

        return paymentMethodMapper.toPaymentMethodResponseList(paymentMethods);
    }

}