package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.*;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.Cart;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.CartServiceImpl;
import com.compass.e_commerce.service.PasswordResetServiceImpl;
import com.compass.e_commerce.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {

    private final UserServiceImpl userServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetServiceImpl passwordResetServiceImpl;
    private final TokenService tokenService;
    private final CartServiceImpl cartServiceImpl;

    @PostMapping("/v1/login")
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Login bem sucedido")
    @ApiResponse(responseCode = "401", description = "Credencias incorretas")
    @ApiResponse(responseCode = "403", description = "Usuario está inativo")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserLoginDetailsDto> login(@RequestBody @Valid UserAuthenticationDto authenticationDto) {
        var user = userServiceImpl.findByLogin(authenticationDto.login());

        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }

        var loginPassword = new UsernamePasswordAuthenticationToken(authenticationDto.login(), authenticationDto.password());
        var auth = this.authenticationManager.authenticate(loginPassword);

        var token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());

        UriComponentsBuilder dummyUriBuilder = UriComponentsBuilder.newInstance();
        UserLoginDetailsDto userLoginDetailsDto = new UserLoginDetailsDto(token);

        userLoginDetailsDto.add(linkTo(methodOn(AuthenticationController.class).register(null, dummyUriBuilder)).withRel("register"));
        userLoginDetailsDto.add(linkTo(methodOn(AuthenticationController.class).registerAdmin(null, dummyUriBuilder)).withRel("register admin"));
        userLoginDetailsDto.add(linkTo(methodOn(AuthenticationController.class).resetPassword("token", null)).withRel("resetPassword"));
        return ResponseEntity.ok(userLoginDetailsDto);
    }

    @PostMapping("/v1/registerUser")
    @ApiResponse(responseCode = "201", description = "Registro bem sucedido")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserDetailsDto> register(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userServiceImpl.convertDtoToEntity(userRegistrationDto);
        cartServiceImpl.associateCartWithUser(user);
        userServiceImpl.registerUser(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        UserDetailsDto userDetailsDto = new UserDetailsDto(user);
        UriComponentsBuilder dummyUriBuilder = UriComponentsBuilder.newInstance();
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).login(null)).withRel("login"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).registerAdmin(null, dummyUriBuilder)).withRel("register admin"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).resetPassword("token", null)).withRel("resetPassword"));
        return ResponseEntity.created(uri).body(userDetailsDto);
    }

    @PostMapping("/v1/registerAdmin")
    @ApiResponse(responseCode = "201", description = "Registro bem sucedido")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserDetailsDto> registerAdmin(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userServiceImpl.convertDtoToEntity(userRegistrationDto);
        cartServiceImpl.associateCartWithUser(user);
        userServiceImpl.registerUserAdmin(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        UserDetailsDto userDetailsDto = new UserDetailsDto(user);
        UriComponentsBuilder dummyUriBuilder = UriComponentsBuilder.newInstance();
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).login(null)).withRel("login"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).register(null, dummyUriBuilder)).withRel("register"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).resetPassword("token", null)).withRel("resetPassword"));
        return ResponseEntity.created(uri).body(userDetailsDto);
    }


    @PostMapping("/v1/requestResetPassword")
    @Operation(summary = "Reset Password")
    @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso")
    @ApiResponse(responseCode = "404", description = "Dado invalido")
    @ApiResponse(responseCode = "401", description = "token invalido")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Void> resetPassword(@RequestParam("token") String token, @RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        passwordResetServiceImpl.processPasswordReset(resetPasswordDto.newPassword(), token);

        return ResponseEntity.noContent().build();
    }

}
