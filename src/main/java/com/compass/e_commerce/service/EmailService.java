package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private  UserService usersService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PasswordResetService passwordResetService;

    public void sendPasswordResetEmail(EmailDto email) {
        User user = validateEmail(email.email());
        SimpleMailMessage message = new SimpleMailMessage();
        String token = passwordResetService.generateTokenReset(email.email());
        message.setTo(email.email());
        message.setSubject("Reset password");
        message.setText("Token to reset the password:" + token);
        mailSender.send(message);
    }

    public User validateEmail(String email) {
        User user = usersService.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("Non-existent email");
        }
        return user;
    }
}
