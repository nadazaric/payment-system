package com.sep.bank.back.feature_qr.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sep.bank.back.feature_qr.service.interf.IpsQrImageService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class IpsQrImageServiceImpl implements IpsQrImageService {

    private static final int QR_IMAGE_SIZE = 280;
    private static final String QR_IMAGE_FORMAT = "PNG";

    @Override
    public String generateBase64Png(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("QR payload must not be empty.");
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = Map.of(
                    EncodeHintType.CHARACTER_SET,
                    StandardCharsets.UTF_8.name()
            );

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    payload,
                    BarcodeFormat.QR_CODE,
                    QR_IMAGE_SIZE,
                    QR_IMAGE_SIZE,
                    hints
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    QR_IMAGE_FORMAT,
                    outputStream
            );

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException exception) {
            throw new IllegalStateException("QR code could not be generated.", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("QR image could not be created.", exception);
        }
    }

}