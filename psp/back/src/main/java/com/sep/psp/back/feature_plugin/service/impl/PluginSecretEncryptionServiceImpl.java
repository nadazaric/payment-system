package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_plugin.service.interf.PluginSecretEncryptionService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PluginSecretEncryptionServiceImpl implements PluginSecretEncryptionService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int AES_256_KEY_LENGTH = 32;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.plugin.encryption-key}")
    String encryptionKeyBase64;

    @Override
    public String encrypt(String plainSecret) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    )
            );

            byte[] encryptedBytes = cipher.doFinal(
                    plainSecret.getBytes(StandardCharsets.UTF_8)
            );

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedBytes);

            return Base64.getEncoder()
                    .encodeToString(byteBuffer.array());
        } catch (Exception exception) {
            throw new BadRequestException("Could not encrypt plugin secret.");
        }
    }

    @Override
    public String decrypt(String encryptedSecret) {
        try {
            byte[] encryptedPayload = Base64.getDecoder().decode(encryptedSecret);

            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedPayload);

            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedBytes);

            Cipher cipher = Cipher.getInstance(AES_GCM_ALGORITHM);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(),
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    )
            );

            byte[] plainBytes = cipher.doFinal(encryptedBytes);

            return new String(
                    plainBytes,
                    StandardCharsets.UTF_8
            );
        } catch (Exception exception) {
            throw new BadRequestException("Could not decrypt plugin secret.");
        }
    }

    private SecretKeySpec getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);

        if (keyBytes.length != AES_256_KEY_LENGTH) {
            throw new IllegalStateException("Plugin secret encryption key must be 32 bytes long.");
        }

        return new SecretKeySpec(
                keyBytes,
                AES_ALGORITHM
        );
    }
}