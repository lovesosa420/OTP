package ru.otp_codes.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otp_codes.dto.OTPConfigDto;
import ru.otp_codes.dto.UsersDelDto;
import ru.otp_codes.service.AdminService;
import ru.otp_codes.utils.JWTValidator;
import ru.otp_codes.utils.JsonParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class AdminController implements HttpHandler {

    private final AdminService adminService = new AdminService();
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (path.startsWith("/admin/")){
          String id = JWTValidator.checkJWT(exchange, List.of("admin"));
          if (id==null){
              sendResponse(exchange, "Missing or invalid Authorization header", 401);
              logger.error("Missing or invalid Authorization header");
              return;
          }
        }
        if (method.equalsIgnoreCase("POST") && path.equals("/admin/otp_config_edit")) {
            handleOtpConfigEdit(exchange);
        } else if (method.equalsIgnoreCase("GET") && path.equals("/admin/users")) {
            handleUsers(exchange);
        } else if (method.equalsIgnoreCase("DELETE") && path.equals("/admin/delete_users")) {
            handleDeleteUsers(exchange);
        }
    }


    private void handleOtpConfigEdit(HttpExchange exchange) {
        try {
            OTPConfigDto otpConfigDto = JsonParser.parseBody(exchange, OTPConfigDto.class);
            adminService.editConfig(otpConfigDto);
            sendResponse(exchange, "Configs were changed successfully", 200);
            logger.info("Configs were changed successfully on lifetime={}, length={}",
                    otpConfigDto.getLifetime(), otpConfigDto.getLength());
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 400);
            logger.error("Failed to set configs, error = {}", ex.getMessage());
        }
    }

    private void handleUsers(HttpExchange exchange) {
        try {
            List<String> usernames = adminService.getUsers();
            sendResponse(exchange, "Usernames: : " + usernames, 200);
            logger.info("Successfully got users");
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 500);
            logger.error("Failed to get users, error={}", ex.getMessage());
        }
    }

    private void handleDeleteUsers(HttpExchange exchange) {
        try {
            UsersDelDto usersDelDto = JsonParser.parseBody(exchange, UsersDelDto.class);
            adminService.deleteUsers(usersDelDto);
            sendResponse(exchange, "Users deleted successfully", 200);
            logger.info("Successfully deleted users");
        } catch (Exception ex) {
            sendResponse(exchange, ex.getMessage(), 500);
            logger.error("Failed to delete users, error={}", ex.getMessage());
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

