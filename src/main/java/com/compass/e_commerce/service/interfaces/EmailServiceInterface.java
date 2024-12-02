package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;

public interface EmailServiceInterface {
    void sendEmail(EmailDto destinationEmail, String emailSubject, String emailText);
    void sendPasswordResetEmail(EmailDto email, String emailSubject, String emailText);
    User validateEmail(String email);
}
