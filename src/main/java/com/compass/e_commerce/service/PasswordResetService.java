package com.compass.e_commerce.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.interfaces.PasswordResetServiceImp;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements PasswordResetServiceImp {

    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public String initiatePasswordReset(String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("E-mail inexistente email:" + email);
        }
        String token = tokenService.generateTokenResetPassword(user);

        return token;
    }

    public void changePassword(String newPassword, String token) {
        try {
            String login = tokenService.getSubject(token);
            User user = userService.findByLogin(login);
            userService.changePassword(user, passwordEncoder.encode(newPassword));

        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido ou expirado", exception);
        }
    }
}

