package ru.otp_codes.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OTPSender {
    private static Logger logger = LoggerFactory.getLogger(OTPSender.class);

    public static boolean sendToFile(String username, String code) {
        try (FileWriter writer = new FileWriter("OTP_" + username + ".txt")) {
            writer.write("Hello, " + username + "\nYour OTP code is: " + code +
                    "\nGenerated at: " + LocalDateTime.now());
        } catch (IOException e) {
            logger.error("Error={} during send OTP-code to file", e.getMessage());
            return false;
        }
        return true;
    }

}
