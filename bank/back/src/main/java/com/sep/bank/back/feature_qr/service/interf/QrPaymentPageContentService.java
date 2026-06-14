package com.sep.bank.back.feature_qr.service.interf;

import com.sep.bank.back.feature_qr.dto.QrPaymentPageContentDTO;

import java.util.UUID;

public interface QrPaymentPageContentService {

    QrPaymentPageContentDTO buildContent(UUID paymentId);

}