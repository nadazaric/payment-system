package com.sep.psp.back.feature_merchant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Merchant {

    @Id
    @Column(nullable = false, updatable = false)
    private String merchantId;

    @Column(nullable = false)
    private String merchantName;

    @Column(nullable = false)
    private String merchantPasswordHash;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String successUrl;

    @Column(nullable = false)
    private String failUrl;

    @Column(nullable = false)
    private String errorUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Merchant(
            String merchantId,
            String merchantName,
            String merchantPasswordHash,
            String currency,
            String successUrl,
            String failUrl,
            String errorUrl
    ) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.merchantPasswordHash = merchantPasswordHash;
        this.currency = currency;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.errorUrl = errorUrl;
        this.active = false;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}