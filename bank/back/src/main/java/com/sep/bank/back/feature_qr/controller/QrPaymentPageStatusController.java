package com.sep.bank.back.feature_qr.controller;

import com.sep.bank.back.feature_qr.dto.QrPaymentPageStatusResponse;
import com.sep.bank.back.feature_qr.service.interf.QrPaymentPageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bank/qr/payments")
public class QrPaymentPageStatusController {

    @Autowired
    QrPaymentPageStatusService qrPaymentPageStatusService;

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<QrPaymentPageStatusResponse> getPaymentPageStatus(
            @PathVariable UUID paymentId
    ) {
        return qrPaymentPageStatusService.getPaymentPageStatus(paymentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}