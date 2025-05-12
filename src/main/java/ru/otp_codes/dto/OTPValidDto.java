package ru.otp_codes.dto;

public class OTPValidDto {

    private String code;

    public OTPValidDto() {
    }

    public OTPValidDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
