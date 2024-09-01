package com.compass.e_commerce.service;

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
    private StringRedisTemplate redisTemplate;

    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public String generateTokenReset(String email) {
        User user = userService.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("Non-existent email");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(token, user.getLogin(), 3, TimeUnit.MINUTES);

        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        String login = redisTemplate.opsForValue().get(token);
        if (login == null) {
            return false;
        }
        return true;
    }

    public void changePassword(String newPassword, String token) {
        if(!validatePasswordResetToken(token)) {
            throw new RuntimeException("Token is invalid or expired");
        }
        String login = redisTemplate.opsForValue().get(token);
        User user = userService.findByLogin(login);
        userService.changePassword(user, passwordEncoder.encode(newPassword));

        redisTemplate.delete(token);
    }
}
