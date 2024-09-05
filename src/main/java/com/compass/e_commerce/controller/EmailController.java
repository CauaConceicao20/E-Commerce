package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/sendEmailForgotPassword")
    public ResponseEntity<Void> sendEmailResetPassword(@RequestBody @Valid EmailDto emailDto) {
        emailService.sendPasswordResetEmail(emailDto);
        return ResponseEntity.ok().build();
    }
}
