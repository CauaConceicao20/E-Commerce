package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.user.ResetPasswordDto;
import com.compass.e_commerce.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resetPassword")
@RequiredArgsConstructor
@Tag(name = "Reset Password")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    @Operation(summary = "Reset Password")
    @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "404", description = "Dado invalido")
    @ApiResponse(responseCode = "401", description = "token invalido")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        passwordResetService.changePassword(resetPasswordDto.newPassword(), resetPasswordDto.token());
        return ResponseEntity.noContent().build();
    }


}
