package com.compass.e_commerce.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.interfaces.PasswordResetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserServiceImpl userServiceImpl;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String initiatePasswordReset(String email) {
        User user = userServiceImpl.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("E-mail inexistente email:" + email);
        }
        String token = tokenService.generateTokenResetPassword(user);

        return token;
    }

    @Override
    public void processPasswordReset(String newPassword, String token) {
        try {
            String login = tokenService.getSubject(token);
            User user = userServiceImpl.findByLogin(login);
            userServiceImpl.changePassword(user, passwordEncoder.encode(newPassword));

        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido ou expirado", exception);
        }
    }
}

