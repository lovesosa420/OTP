package ru.otp_codes.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public static boolean verify(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}

