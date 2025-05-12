package ru.otp_codes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otp_codes.dao.UserDao;
import ru.otp_codes.dto.UserDto;
import ru.otp_codes.dto.UserRegistrationDto;
import ru.otp_codes.model.User;
import ru.otp_codes.utils.PasswordEncoder;
import ru.otp_codes.utils.UserMapper;

import javax.crypto.spec.SecretKeySpec;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;

import io.jsonwebtoken.*;

import java.util.*;

import io.jsonwebtoken.Jwts;

public class AuthService {
    private UserDao userDao;
    private static String SECRET_KEY;
    private int ttlMillis;
    private static Logger logger;

    public AuthService() {
        Properties config = jwtConfig();
        logger = LoggerFactory.getLogger(AuthService.class);
        this.userDao = new UserDao();
        SECRET_KEY = config.getProperty("app.secret_key");
        this.ttlMillis = Integer.parseInt(config.getProperty("app.ttlMillis"));
    }

    private Properties jwtConfig() {
        try {
            Properties props = new Properties();
            props.load(AuthService.class.getClassLoader()
                    .getResourceAsStream("app.properties"));
            return props;
        } catch (Exception e) {
            logger.error("Failed to load JWT configuration");
            throw new RuntimeException("Failed to load JWT configuration", e);
        }
    }

    public String register(UserRegistrationDto UserRegistrationDto) throws Exception {
        if (userDao.findByUsername(UserRegistrationDto.getUsername()) != null)
            throw new Exception("Username already taken");

        if (UserRegistrationDto.isAdmin() && userDao.isAdminExists())
            throw new Exception("Admin already exists");

        User user = UserMapper.fromDto(UserRegistrationDto);
        userDao.saveUser(user);

        return "Registered successfully.";
    }

    public String login(UserDto userDto) throws Exception {
        User user = userDao.findByUsername(userDto.getUsername());
        if (user == null)
            return null;
        if (PasswordEncoder.verify(userDto.getPassword(), user.getPasswordHash())) {
            return generateJWT(user);
        } else {
            return null;
        }
    }

    public String generateJWT(User user) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getUsername());
        claims.put("role", user.getRole());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuer("otp_codes")
                .signWith(SignatureAlgorithm.HS256, signingKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Map<String, String> decodeJWT(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                    .parseClaimsJws(jwt)
                    .getBody();

            Map<String, String> result = new HashMap<>();
            result.put("role", (String) claims.get("role"));
            result.put("id", claims.getSubject());
            return result;
        } catch (ExpiredJwtException e) {
            logger.error("Token expired: {}", e.getMessage());
            return null;
        } catch (SignatureException e) {
            logger.error("Invalid signature: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Invalid token: {}", e.getMessage());
            return null;
        }
    }
}
