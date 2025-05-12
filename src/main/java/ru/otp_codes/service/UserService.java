package ru.otp_codes.service;

import ru.otp_codes.dao.OTPConfigDao;
import ru.otp_codes.dao.OTPDao;
import ru.otp_codes.dao.TransactionDao;
import ru.otp_codes.dao.UserDao;
import ru.otp_codes.dto.OTPDto;
import ru.otp_codes.dto.OTPValidDto;
import ru.otp_codes.dto.TransactionDto;
import ru.otp_codes.model.OTPCode;
import ru.otp_codes.model.User;
import ru.otp_codes.utils.OTPSender;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private final OTPDao otpDao = new OTPDao();
    private final UserDao userDao = new UserDao();

    private final OTPConfigDao otpConfigDao = new OTPConfigDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private final EmailNotificationService emailNotificationService = new EmailNotificationService();
    private final SmsNotificationService smsNotificationService = new SmsNotificationService();
    private final TelegramNotificationService telegramNotificationService = new TelegramNotificationService();

    public Map<String, Boolean> generateOTP(OTPDto otpDto, String id) throws Exception {

        Map<String, Integer> otpConfig = otpConfigDao.getLengthAndLifetime();

        String code = generateNumericCode(otpConfig.get("length"));
        OTPCode otp = new OTPCode();
        otp.setCode(code);
        otp.setStatus("ACTIVE");
        otp.setExpiresAt(LocalDateTime.now().plusSeconds(otpConfig.get("lifetime")));
        otp.setTransactionId(otpDto.getTransactionId());

        otpDao.saveOTP(otp);
        User user = userDao.findById(id);

        Map<String, Boolean> result = new HashMap<>();
        if (otpDto.isSaveToFileOTP()) {
            result.put("file", OTPSender.sendToFile(user.getUsername(), code));
        }
        if (otpDto.isSendOTP()) {
            result.put("email", emailNotificationService.sendCode(user.getEmail(), code));
            result.put("sms", smsNotificationService.sendCode(user.getPhoneNumber(), code));
            result.put("telegram", telegramNotificationService.sendCode(user, code));
        }
        return result;
    }

    public String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    public boolean validateOTP(OTPValidDto otpValidDto, String id) throws SQLException {
        OTPCode otpCode = otpDao.findOTPByCodeAndUserId(otpValidDto.getCode(), id);
        if (Objects.equals(otpCode.getStatus(), "ACTIVE") && otpCode.getExpiresAt().isAfter(LocalDateTime.now())) {
            otpDao.editStatus(otpCode.getId(), "USED");
            transactionDao.validateTransaction(otpCode.getTransactionId());
            return true;
        }
        return false;
    }

    public void makeTransaction(TransactionDto transactionDto, String id) throws SQLException {
        transactionDao.saveTransaction(transactionDto, UUID.fromString(id));
    }
}
