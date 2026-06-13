package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_qr.dto.IpsQrPayloadData;
import com.sep.bank.back.feature_qr.dto.QrScanResponse;
import com.sep.bank.back.feature_qr.service.interf.IpsQrValidatorService;
import com.sep.bank.back.feature_qr.service.interf.QrScanService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QrScanServiceImpl implements QrScanService {

    @Autowired
    IpsQrValidatorService ipsQrValidatorService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public QrScanResponse scan(String payload) {
        try {
            IpsQrPayloadData payloadData = ipsQrValidatorService.validateAndParse(payload);

            return new QrScanResponse(
                    payloadData.paymentReference(),
                    payloadData.recipientName(),
                    payloadData.recipientAccount(),
                    payloadData.amount(),
                    payloadData.currency(),
                    payloadData.paymentPurpose()
            );
        } catch (IllegalArgumentException exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.QR_SCAN_REJECTED,
                    "reason={} error={}",
                    LogStrings.Reason.INVALID_QR_PAYLOAD,
                    exception.getMessage()
            );

            throw exception;
        }
    }

}