package com.sep.bank.back.feature_qr.service.interf;

import com.sep.bank.back.feature_qr.dto.IpsQrPayloadData;

public interface IpsQrValidatorService {

    IpsQrPayloadData validateAndParse(String payload);

}