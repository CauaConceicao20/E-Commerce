package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.user.EmailDto;
import com.compass.e_commerce.service.EmailServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class EmailController {

    private final EmailServiceImpl emailServiceImpl;

    @PostMapping("/v1/sendEmailForgotPassword")
    @Operation(summary = "Send Password Reset Email")
    @ApiResponse(responseCode = "204", description = "Email enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados Invalidos")
    @ApiResponse(responseCode = "404", description = "Email não existe")
    public ResponseEntity<Void> sendEmailResetPassword(@RequestBody @Valid EmailDto emailDto) {
        emailServiceImpl.sendPasswordResetEmail(emailDto.email(), "Reset password", "Token para redefinir a senha: ");
        return ResponseEntity.noContent().build();
    }
}
