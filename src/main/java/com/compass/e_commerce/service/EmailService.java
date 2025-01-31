package com.compass.e_commerce.service;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.interfaces.EmailServiceImp;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServiceImp {

    private final JavaMailSender mailSender;
    private final UserService usersService;
    private final PasswordResetService passwordResetService;


    public void sendEmail(String destinationEmail, String emailSubject, String emailText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinationEmail);
        message.setSubject(emailSubject);
        message.setText(emailText);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String destinationEmail, String emailSubject, String emailText) {
        User user = validateEmail(destinationEmail);
        String token = passwordResetService.initiatePasswordReset(destinationEmail);
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
