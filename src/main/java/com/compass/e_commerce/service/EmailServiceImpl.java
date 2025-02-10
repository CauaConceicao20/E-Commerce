package com.compass.e_commerce.service;

import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.interfaces.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService<User> {

    private final JavaMailSender mailSender;
    private final UserServiceImpl usersService;
    private final PasswordResetServiceImpl passwordResetServiceImpl;

    @Override
    public void sendEmail(String destinationEmail, String emailSubject, String emailText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinationEmail);
        message.setSubject(emailSubject);
        message.setText(emailText);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String destinationEmail, String emailSubject, String emailText) {
        User user = validateEmail(destinationEmail);
        String token = passwordResetServiceImpl.initiatePasswordReset(destinationEmail);
        sendEmail(destinationEmail, emailSubject, emailText + token);

    }

    @Override
    public User validateEmail(String email) {
        User user = usersService.findByEmail(email);
        if(user == null) {
            throw new EntityNotFoundException("E-mail inexistente email: " + email);
        }
        return user;
    }
}
