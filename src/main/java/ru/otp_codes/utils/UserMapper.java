package ru.otp_codes.utils;

import ru.otp_codes.dto.UserRegistrationDto;
import ru.otp_codes.model.User;

public class UserMapper {
    public static User fromDto(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPasswordHash(PasswordEncoder.hash(dto.getPassword()));
        user.setTelegramUsername(dto.getTelegramUsername());
        String role = dto.isAdmin() ? "admin" : "user";
        user.setRole(role);
        return user;
    }

}
