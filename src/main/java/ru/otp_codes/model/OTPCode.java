package ru.otp_codes.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OTPCode {
    private UUID id;
    private UUID transactionId;
    private String code;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public OTPCode(UUID id, String status, UUID transactionId, LocalDateTime expiresAt) {
        this.id = id;
        this.status = status;
        this.transactionId =transactionId;
        this.expiresAt = expiresAt;
    }

    public OTPCode() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
