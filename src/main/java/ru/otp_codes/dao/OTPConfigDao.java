package ru.otp_codes.dao;

import ru.otp_codes.dto.OTPConfigDto;
import ru.otp_codes.utils.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OTPConfigDao {
    public void createOTPConfig(int lifetime, int length) throws SQLException {
        String sqlCreate = """
                CREATE TABLE IF NOT EXISTS otp_config (
                    id INTEGER PRIMARY KEY,
                    lifetime INTEGER,
                    length INTEGER
                );""";
        String sqlInsert = """
                INSERT INTO otp_config (id, lifetime, length)
                SELECT 1, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM otp_config
                );;""";
        try (Connection conn = DB.getConnection();
             PreparedStatement psCreate = conn.prepareStatement(sqlCreate);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert);) {
            psCreate.executeUpdate();
            psInsert.setInt(1, lifetime);
            psInsert.setInt(2, length);
            psInsert.executeUpdate();
        }
    }

    public void setLengthAndLifetime(OTPConfigDto otpConfigDto) throws SQLException {
        String sql = "UPDATE otp_config SET length=?, lifetime=? WHERE id = 1";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, otpConfigDto.getLength());
            ps.setInt(2, otpConfigDto.getLifetime());
            ps.executeUpdate();
        }
    }

    public Map<String, Integer> getLengthAndLifetime() throws SQLException {
        String sql = "SELECT length, lifetime FROM otp_config WHERE id = 1";
        Map<String, Integer> otpConfig = new HashMap<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                otpConfig.put("length", rs.getInt("length"));
                otpConfig.put("lifetime", rs.getInt("lifetime"));
            }
        }
        return otpConfig;
    }
}
