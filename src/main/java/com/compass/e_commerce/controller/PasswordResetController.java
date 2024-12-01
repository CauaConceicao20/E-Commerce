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
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/resetPassword")
@RequiredArgsConstructor
@Tag(name = "Reset Password")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/v1/request")
    @Operation(summary = "Reset Password")
    @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "404", description = "Dado invalido")
    @ApiResponse(responseCode = "401", description = "token invalido")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<EntityModel<Void>> resetPassword(@RequestParam("token") String token, @RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        passwordResetService.changePassword(resetPasswordDto.newPassword(), token);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        EntityModel<Void> response = EntityModel.of(null);
        response.add(linkTo(methodOn(AuthenticationController.class).login(null)).withRel("login"));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }


}
