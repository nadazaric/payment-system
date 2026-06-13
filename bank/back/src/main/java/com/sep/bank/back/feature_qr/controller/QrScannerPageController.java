package com.sep.bank.back.feature_qr.controller;

import com.sep.bank.back.feature_qr.dto.QrScanRequest;
import com.sep.bank.back.feature_qr.dto.QrScanResponse;
import com.sep.bank.back.feature_qr.service.interf.QrScanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/mock-mbanking")
public class QrScannerPageController {

    private static final String QR_SCANNER_PAGE = "classpath:payment-pages/mock-mbanking-qr-scanner-page.html";

    private final ResourceLoader resourceLoader;

    @Autowired
    QrScanService qrScanService;

    public QrScannerPageController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/qr-scanner")
    public ResponseEntity<String> getQrScannerPage() {
        return noCacheResponse(HttpStatus.OK).body(loadTemplate());
    }

    private ResponseEntity.BodyBuilder noCacheResponse(HttpStatus status) {
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentType(MediaType.TEXT_HTML);
    }

    private String loadTemplate() {
        try {
            Resource resource = resourceLoader.getResource(QR_SCANNER_PAGE);

            return StreamUtils.copyToString(
                    resource.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (Exception exception) {
            throw new IllegalStateException("QR scanner page template could not be loaded.");
        }
    }

    @PostMapping("/scan")
    public ResponseEntity<QrScanResponse> scan(
            @Valid @RequestBody QrScanRequest request
    ) {
        QrScanResponse response = qrScanService.scan(request.payload());

        return ResponseEntity.ok(response);
    }

}