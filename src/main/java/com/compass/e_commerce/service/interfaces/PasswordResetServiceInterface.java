package com.compass.e_commerce.service.interfaces;

public interface PasswordResetServiceInterface {
    String generateTokenReset(String email);
    void changePassword(String newPassword, String token);
}
