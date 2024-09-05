package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserService usersService;
    private final PasswordResetService passwordResetService;

    public void sendPasswordResetEmail(EmailDto email) {
        User user = validateEmail(email.email());
        SimpleMailMessage message = new SimpleMailMessage();
        String token = passwordResetService.generateTokenReset(email.email());
        message.setTo(email.email());
        message.setSubject("Reset password");
        message.setText("Token para redefinir a senha: " + token);
        mailSender.send(message);
    }

    public User validateEmail(String email) {
        User user = usersService.findByEmail(email);
        if(user == null) {
            throw new EntityNotFoundException("E-mail inexistente email: " + email);
        }
        return user;
    }
}
