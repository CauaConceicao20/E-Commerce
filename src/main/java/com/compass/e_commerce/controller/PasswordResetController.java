package com.compass.e_commerce.controller;

import com.compass.e_commerce.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resetPassword")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request")
    public ResponseEntity<Void> resetPassword(@RequestParam("newPassword") String newPassword, @RequestParam("token") String token ) {
        passwordResetService.changePassword(newPassword, token);
        return ResponseEntity.ok().build();
    }


}
