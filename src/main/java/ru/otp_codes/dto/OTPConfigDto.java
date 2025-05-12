package ru.otp_codes.dto;

public class OTPConfigDto {
    private int length;
    private int lifetime;

    public OTPConfigDto() {
    }

    public OTPConfigDto(int length, int lifetime) {
        this.length = length;
        this.lifetime = lifetime;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
