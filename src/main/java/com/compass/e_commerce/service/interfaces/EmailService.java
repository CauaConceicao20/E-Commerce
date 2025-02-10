package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.User;

public interface EmailService<T> {
    void sendEmail(String destinationEmail, String emailSubject, String emailText);
    void sendPasswordResetEmail(String email, String emailSubject, String emailText);
    T validateEmail(String email);
}
