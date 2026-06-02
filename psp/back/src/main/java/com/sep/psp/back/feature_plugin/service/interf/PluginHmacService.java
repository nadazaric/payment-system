package com.sep.psp.back.feature_plugin.service.interf;

public interface PluginHmacService {

    String generateSignature(String secret, String timestamp, String requestBody);

    Boolean isSignatureValid(String secret, String timestamp, String requestBody, String receivedSignature);

}