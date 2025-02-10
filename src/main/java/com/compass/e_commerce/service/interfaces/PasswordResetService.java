package com.compass.e_commerce.service.interfaces;

public interface PasswordResetService {
    String initiatePasswordReset(String email);
    void processPasswordReset(String newPassword, String token);
}
