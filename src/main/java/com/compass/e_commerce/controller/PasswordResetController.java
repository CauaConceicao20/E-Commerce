package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.user.ResetPasswordDto;
import com.compass.e_commerce.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resetPassword")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        passwordResetService.changePassword(resetPasswordDto.newPassword(), resetPasswordDto.token());
        return ResponseEntity.ok().build();
    }


}
