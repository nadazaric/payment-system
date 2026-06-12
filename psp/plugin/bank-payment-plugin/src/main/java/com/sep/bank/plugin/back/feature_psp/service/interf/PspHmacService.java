package com.sep.bank.plugin.back.feature_psp.service.interf;

public interface PspHmacService {

    String generateSignature(String secret, String timestamp, String requestBody);

    boolean isSignatureValid(String secret, String timestamp, String requestBody, String receivedSignature);

}
