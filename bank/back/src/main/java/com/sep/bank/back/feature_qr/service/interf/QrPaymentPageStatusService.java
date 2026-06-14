package com.sep.bank.back.feature_qr.service.interf;

import com.sep.bank.back.feature_qr.dto.QrPaymentPageStatusResponse;

import java.util.Optional;
import java.util.UUID;

public interface QrPaymentPageStatusService {

    Optional<QrPaymentPageStatusResponse> getPaymentPageStatus(UUID paymentId);

}