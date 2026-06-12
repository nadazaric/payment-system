package com.sep.bank.back.shared.security.service.interf;

public interface HmacService {

    String generateSignature(
            String secret,
            String timestamp,
            String requestBody
    );

    boolean isSignatureValid(
            String secret,
            String timestamp,
            String requestBody,
            String receivedSignature
    );

}