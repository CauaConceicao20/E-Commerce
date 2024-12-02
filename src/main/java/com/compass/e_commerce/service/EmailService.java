package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.interfaces.EmailServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceInterface {

    private final JavaMailSender mailSender;
    private final UserService usersService;
    private final PasswordResetService passwordResetService;


    public void sendEmail(EmailDto destinationEmail, String emailSubject, String emailText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinationEmail.email());
        message.setSubject(emailSubject);
        message.setText(emailText);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(EmailDto destinationEmail, String emailSubject, String emailText) {
        User user = validateEmail(destinationEmail.email());
        String token = passwordResetService.generateTokenReset(destinationEmail.email());
        sendEmail(destinationEmail, emailSubject, emailText + token);

    }

    public User validateEmail(String email) {
        User user = usersService.findByEmail(email);
        if(user == null) {
            throw new EntityNotFoundException("E-mail inexistente email: " + email);
        }
        return user;
    }
}
