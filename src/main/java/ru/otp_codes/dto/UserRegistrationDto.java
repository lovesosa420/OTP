package ru.otp_codes.dto;

public class UserRegistrationDto {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private boolean isAdmin;
    private String telegramUsername;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String username, String password, String email, String phoneNumber, boolean isAdmin,
            String telegramUsername) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
        this.telegramUsername = telegramUsername;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
