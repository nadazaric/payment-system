package com.sep.bank.back.feature_qr.service.interf;

import com.sep.bank.back.feature_qr.dto.QrPaymentRequest;
import com.sep.bank.back.feature_qr.dto.QrPaymentResponse;

public interface QrPaymentProcessingService {

    QrPaymentResponse submitQrPayment(QrPaymentRequest request);

}