package com.sep.bank.back.feature_payment.service.interf;

public interface CardPanHashService {

    String generatePanHash(String normalizedPan);

}