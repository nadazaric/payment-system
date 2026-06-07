package com.sep.psp.back.feature_plugin.service.interf;

public interface PluginSecretEncryptionService {

    String encrypt(String plainSecret);

    String decrypt(String encryptedSecret);

}