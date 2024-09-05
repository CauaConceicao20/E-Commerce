package com.compass.e_commerce.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {

    private final UserService userService;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userService  = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateTokenReset(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("E-mail inexistente email:" + email);
        }
        String token = tokenService.generateTokenResetPassword(user);

        return token;
    }

    public void changePassword(String newPassword, String token) {
        try {
            String login = tokenService.getSubject(token);
            User user = userService.findByLogin(login);
            userService.changePassword(user, passwordEncoder.encode(newPassword));

        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido ou expirado", exception);
        }
    }
}

