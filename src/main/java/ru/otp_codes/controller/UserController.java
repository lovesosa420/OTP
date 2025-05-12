package ru.otp_codes.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.otp_codes.dto.OTPDto;
import ru.otp_codes.dto.OTPValidDto;
import ru.otp_codes.dto.TransactionDto;
import ru.otp_codes.service.UserService;
import ru.otp_codes.utils.JWTValidator;
import ru.otp_codes.utils.JsonParser;

public class UserController implements HttpHandler {
    private final UserService userService = new UserService();
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String id = null;
        if (path.startsWith("/user/")) {
            id = JWTValidator.checkJWT(exchange, List.of("admin", "user"));
            if (id == null) {
                sendResponse(exchange, "Missing or invalid Authorization header", 401);
                logger.error("Missing or invalid Authorization header");
                return;
            }
        }
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            if (path.equals("/user/validate_otp")) handleValidateOTP(exchange, id);
            if (path.equals("/user/generate_otp")) handleGenerateOTP(exchange, id);
            else if (path.equals("/user/make_transaction")) handleTransaction(exchange, id);
        }
    }

    private void handleValidateOTP(HttpExchange exchange, String id) {
        try {
            OTPValidDto otpValidDto = JsonParser.parseBody(exchange, OTPValidDto.class);
            boolean result = userService.validateOTP(otpValidDto, id);
            if (result) {
                sendResponse(exchange, "OTP-code validated successfully", 200);
                logger.info("Successfully validated OTP-code={}", otpValidDto.getCode());
            } else {
                sendResponse(exchange, "OTP-code NOT validated successfully", 400);
                logger.error("OTP-code={} NOT validated successfully", otpValidDto.getCode());
            }
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 400);
            logger.error("Failed to validate OTP-code, error = {}", ex.getMessage());
        }
    }

    private void handleGenerateOTP(HttpExchange exchange, String id) {
        try {
            OTPDto otpDto = JsonParser.parseBody(exchange, OTPDto.class);
            Map<String, Boolean> res = userService.generateOTP(otpDto, id);

            if (otpDto.isSaveToFileOTP()) {
                if (!res.get("file")) {
                    sendResponse(exchange, "Impossible to save to File OTP-Code", 400);
                    logger.error("Impossible to save to File OTP-Code");
                }
            }

            if (otpDto.isSendOTP()) {
                if (!(res.get("email") || res.get("sms") || res.get("telegram"))) {
                    sendResponse(exchange, "Impossible to send OTP-Code via telegram, sms or email", 400);
                    logger.error("Impossible to send OTP-Code via telegram, sms or email");
                }
            }

            String result = res.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining(","));

            if (!result.isEmpty()) {
                sendResponse(exchange, "OTP generated and sent via=" + result, 200);
                logger.info("Successfully OTP-code generated and sent via ={} for transactionId={}",
                        result, otpDto.getTransactionId());
            } else {
                sendResponse(exchange, "OTP generated and NOT send" + result, 200);
                logger.info("Successfully OTP-code generated for transactionId={}", otpDto.getTransactionId());
            }
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 400);
            logger.error("Failed to generate OTP-code, error = {}", ex.getMessage());
        }
    }

    private void handleTransaction(HttpExchange exchange, String id) {
        try {
            TransactionDto transactionDto = JsonParser.parseBody(exchange, TransactionDto.class);
            userService.makeTransaction(transactionDto, id);
            sendResponse(exchange, "Transaction made", 200);
            logger.info("Transaction with purchase={} and amount={} was made",
                    transactionDto.getPurchase(), transactionDto.getAmount());
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 400);
            logger.error("Failed to make transaction, error = {}", ex.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int code) {
        try {
            exchange.sendResponseHeaders(code, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            logger.error("Failer to send resposnse, error={}", e.getMessage());
        }
    }
}
