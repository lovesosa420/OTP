package ru.otp_codes.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otp_codes.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static Logger logger;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public DB() {
        Properties config = dbConfig();
        logger = LoggerFactory.getLogger(DB.class);
        URL = config.getProperty("app.db.url");
        USER =config.getProperty("app.db.user");
        PASSWORD = config.getProperty("app.db.pass");
    }

    private Properties dbConfig() {
        try {
            Properties props = new Properties();
            props.load(Main.class.getClassLoader()
                    .getResourceAsStream("app.properties"));
            return props;
        } catch (Exception e) {
            logger.error("Failed to load DB configuration");
            throw new RuntimeException("Failed to load DB configuration", e);
        }
    }
}
