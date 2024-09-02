package com.compass.e_commerce.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public String generateTokenReset(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Non-existent email");
        }
        String token = tokenService.generateTokenResetPassword(user);

        return token;
    }

    /*
    public boolean validatePasswordResetToken(String token) {
        String login = tokenService.getSubject(token);
        if (login == null) {
            return false;
        }
        return true;
    }

     */

    public void changePassword(String newPassword, String token) {
        try {
            String login = tokenService.getSubject(token);
            User user = userService.findByLogin(login);
            userService.changePassword(user, passwordEncoder.encode(newPassword));

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid or expired token", exception);
        }
    }
}

