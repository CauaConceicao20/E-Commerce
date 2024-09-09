package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.model.User;

public interface EmailServiceInterface {
    void sendPasswordResetEmail(EmailDto email);
    User validateEmail(String email);
}
