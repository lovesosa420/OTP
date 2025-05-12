package ru.otp_codes.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otp_codes.dto.UserDto;
import ru.otp_codes.dto.UserRegistrationDto;
import ru.otp_codes.service.AuthService;
import ru.otp_codes.utils.JsonParser;

import java.io.*;

public class AuthController implements HttpHandler {
    private final AuthService authService = new AuthService();
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            if (path.equals("/register"))
                handleRegister(exchange);
            else if (path.equals("/login"))
                handleLogin(exchange);
        }
    }

    private void handleRegister(HttpExchange exchange) {
        try {
            UserRegistrationDto UserRegistrationDto = JsonParser.parseBody(exchange, UserRegistrationDto.class);
            String res = authService.register(UserRegistrationDto);
            sendResponse(exchange, res, 200);
            logger.info("Successfully register for user with username={}", UserRegistrationDto.getUsername());
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 400);
            logger.error("Failed to register, error = {}", ex.getMessage());
        }
    }

    private void handleLogin(HttpExchange exchange) {
        try {
            UserDto userDto = JsonParser.parseBody(exchange, UserDto.class);
            String token = authService.login(userDto);
            if (token != null) {
                sendResponse(exchange, "Login successful, token: " + token, 200);
                logger.info("Successfully login for user with username={}", userDto.getUsername());
            } else {
                sendResponse(exchange, "Invalid credentials", 401);
                logger.error("Failed to login for username={}", userDto.getUsername());
            }
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 500);
            logger.error("Failed to login, error = {}", ex.getMessage());
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