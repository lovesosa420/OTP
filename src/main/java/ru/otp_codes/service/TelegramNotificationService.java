package ru.otp_codes.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otp_codes.dao.UserDao;
import ru.otp_codes.model.User;

public class TelegramNotificationService {

    private final UserDao userDao;
    private static String BOT_TOKEN;
    private static Logger logger;

    public TelegramNotificationService() {
        Properties config = tgConfig();
        logger = LoggerFactory.getLogger(TelegramNotificationService.class);
        this.userDao = new UserDao();
        BOT_TOKEN = config.getProperty("app.token_bot");
    }

    private Properties tgConfig() {
        try {
            Properties props = new Properties();
            props.load(AuthService.class.getClassLoader()
                    .getResourceAsStream("app.properties"));
            return props;
        } catch (Exception e) {
            logger.error("Failed to load telegram configuration");
            throw new RuntimeException("Failed to load Telegram configuration", e);
        }
    }

    private Long findChatId(User user) {

        Map<String, Long> userChatMap = new HashMap<>();
        try {
            String API_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/getUpdates";

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            JsonReader reader = Json.createReader(is);
            JsonObject jsonObject = reader.readObject();

            if (jsonObject.getBoolean("ok")) {
                JsonArray results = jsonObject.getJsonArray("result");

                for (JsonValue resultValue : results) {
                    JsonObject result = resultValue.asJsonObject();

                    JsonObject message = result.getJsonObject("message");
                    if (message != null) {
                        JsonObject from = message.getJsonObject("from");
                        JsonObject chat = message.getJsonObject("chat");

                        if (from != null && chat != null) {
                            String username = from.getString("username", null);
                            long chatId = chat.getJsonNumber("id").longValue();

                            if (username != null) {
                                userChatMap.put(username, chatId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error={}", e.getMessage());
        }
        return userChatMap.get(user.getTelegramUsername());
    }

    public boolean sendCode(User user, String code) {

        try {
            Long chatId = findChatId(user);
            if (chatId != null) {
                String message = String.format(user.getUsername() + ", your confirmation code is: %s", code);
                String url = String.format("%s?chat_id=%s&text=%s",
                        "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage",
                        chatId,
                        urlEncode(message));
                sendTelegramRequest(url);
                return true;
            } else {
                logger.info("Sending to Telegram impossible for userName={}", user.getUsername());
                return false;
            }
        } catch (IOException e) {
            logger.error("Error={} while sending OTP-code to telegram", e.getMessage());
            return false;
        }
    }

    private void sendTelegramRequest(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    logger.error("Telegram API error. Status code: {}", statusCode);
                } else {
                    logger.info("Telegram message sent successfully");
                }
            }
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}