package ru.otp_codes.dto;

import java.util.UUID;

public class OTPDto {

    private UUID transactionId;
    private boolean saveToFileOTP;
    private boolean sendOTP;

    public OTPDto() {
    }

    public OTPDto(UUID transactionId, boolean saveToFileOTP, boolean sendOTP) {
        this.transactionId = transactionId;
        this.saveToFileOTP = saveToFileOTP;
        this.sendOTP = sendOTP;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isSaveToFileOTP() {
        return saveToFileOTP;
    }

    public void setSaveToFileOTP(boolean saveToFileOTP) {
        this.saveToFileOTP = saveToFileOTP;
    }

    public boolean isSendOTP() {
        return sendOTP;
    }

    public void setSendOTP(boolean sendOTP) {
        this.sendOTP = sendOTP;
    }
}
