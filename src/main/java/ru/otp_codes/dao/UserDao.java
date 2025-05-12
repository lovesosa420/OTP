package ru.otp_codes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.otp_codes.model.User;
import ru.otp_codes.utils.DB;

public class UserDao {
    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, email, phone_number, tg_username) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getTelegramUsername());
            ps.executeUpdate();
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(UUID.fromString(rs.getString("id")));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setTelegramUsername(rs.getString("tg_username"));
                return user;
            }
        }
        return null;
    }

    public User findById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(UUID.fromString(id));
                user.setRole(rs.getString("role"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setTelegramUsername(rs.getString("tg_username"));
                return user;
            }
        }
        return null;
    }

    public List<String> findAllUsernamesWithRoleUser() throws SQLException {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT username FROM users WHERE role = 'user'";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        }
        return usernames;
    }

    public void deleteUsersByUsernames(List<String> usernames) throws SQLException {
        String sql = "DELETE FROM users WHERE username=?";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String username : usernames) {
                ps.setString(1, username);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean isAdminExists() throws SQLException {
        String sql = "SELECT 1 FROM users WHERE role='admin'";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    public void createUser() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) NOT NULL,
                    phone_number VARCHAR(15) NOT NULL,
                    tg_username VARCHAR(20) NOT NULL,
                    password_hash TEXT NOT NULL,
                    role VARCHAR(20) NOT NULL
                );""";
        try (Connection conn = DB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
