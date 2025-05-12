package ru.otp_codes.dto;

import java.util.List;

public class UsersDelDto {

    private List<String> usernames;

    public UsersDelDto() {
    }

    public UsersDelDto(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
