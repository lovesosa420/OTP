package ru.otp_codes.service;

import ru.otp_codes.dao.OTPConfigDao;
import ru.otp_codes.dao.UserDao;
import ru.otp_codes.dto.OTPConfigDto;
import ru.otp_codes.dto.UsersDelDto;

import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private final OTPConfigDao otpConfigDao = new OTPConfigDao();
    private final UserDao userDao = new UserDao();

    public void editConfig(OTPConfigDto otpConfigDto) throws SQLException {
        otpConfigDao.setLengthAndLifetime(otpConfigDto);
    }

    public List<String> getUsers() throws SQLException {
        return userDao.findAllUsernamesWithRoleUser();
    }

    public void deleteUsers(UsersDelDto usersDelDto) throws SQLException {
        userDao.deleteUsersByUsernames(usersDelDto.getUsernames());
    }
}
