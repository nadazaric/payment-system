package com.sep.bank.back.feature_qr.service.interf;

import com.sep.bank.back.feature_qr.dto.QrScanResponse;

public interface QrScanService {

    QrScanResponse scan(String payload);

}