package com.compass.e_commerce.service.interfaces;

public interface PasswordResetServiceImp {
    String generateTokenReset(String email);
    void changePassword(String newPassword, String token);
}
