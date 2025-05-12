package ru.otp_codes.utils;

import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import ru.otp_codes.service.AuthService;

public class JWTValidator {

    public static String checkJWT(HttpExchange exchange, List<String> roles) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String jwtToken = authHeader.substring(7);

        Map<String, String> userAttr = AuthService.decodeJWT(jwtToken);

        if (userAttr == null) {
            return null;
        }

        String roleAttr = userAttr.get("role");

        if (!roles.contains(roleAttr)) {
            return null;
        }

        return userAttr.get("id");
    }

}
