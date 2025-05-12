package ru.otp_codes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

public class JsonParser {

    public static <U> U parseBody(HttpExchange exchange, Class<U> clazz) throws IOException {
        try (InputStream isr = exchange.getRequestBody()) {
            return new ObjectMapper().readValue(isr, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
