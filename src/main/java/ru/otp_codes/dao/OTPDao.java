package ru.otp_codes.dao;

import ru.otp_codes.model.OTPCode;
import ru.otp_codes.utils.DB;

import java.sql.*;
import java.util.UUID;

public class OTPDao {
    public void saveOTP(OTPCode code) throws SQLException {
        String sql = "INSERT INTO otp_codes (transaction_id, code, status, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, code.getTransactionId());
            ps.setString(2, code.getCode());
            ps.setString(3, code.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(code.getExpiresAt()));
            ps.executeUpdate();
        }
    }

    public void editStatus(UUID id, String status) throws SQLException {
        String sql = "UPDATE otp_codes SET status=? where id=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setObject(2, id);
            ps.executeUpdate();
        }
    }

    public void editStatusWhenExpired(String status) throws SQLException {
        String sql = "UPDATE otp_codes SET status=? where expires_at < CURRENT_TIMESTAMP and status!=''USED'";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.executeUpdate();
        }
    }

    public OTPCode findOTPByCodeAndUserId(String code, String id) throws SQLException {
        String sql = "SELECT o.id, o.status, o.expires_at, o.transaction_id FROM otp_codes o " +
                "JOIN transactions tr on tr.id=o.transaction_id " +
                "JOIN users u on u.id=tr.user_id " +
                "WHERE o.code=? and u.id=?";
        OTPCode otpCode = null;
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setObject(2, UUID.fromString(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                otpCode = new OTPCode(
                        (UUID) rs.getObject("id"),
                        rs.getString("status"),
                        (UUID) rs.getObject("transaction_id"),
                        rs.getTimestamp("expires_at").toLocalDateTime());
            }
        }
        return otpCode;
    }

    public void createOTP() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS otp_codes (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    transaction_id UUID REFERENCES transactions(id) ON DELETE CASCADE,
                    code VARCHAR(10) NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT now(),
                    expires_at TIMESTAMP NOT NULL
                );""";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
