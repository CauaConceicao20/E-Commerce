package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.UserAuthenticationDto;
import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserLoginDetailsDto;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.UserService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/v1/login")
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Login bem sucedido")
    @ApiResponse(responseCode = "401", description = "Credencias incorretas")
    @ApiResponse(responseCode = "403", description = "Usuario está inativo")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserLoginDetailsDto> login(@RequestBody @Valid UserAuthenticationDto authenticationDto) {
        var user = userService.findByLogin(authenticationDto.login());

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
        return ResponseEntity.ok(userLoginDetailsDto);
    }

    @PostMapping("/v1/registerUser")
    @ApiResponse(responseCode = "201", description = "Registro bem sucedido")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserDetailsDto> register(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userService.convertDtoToEntity(userRegistrationDto);
        userService.registerUser(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        UserDetailsDto userDetailsDto = new UserDetailsDto(user);
        UriComponentsBuilder dummyUriBuilder = UriComponentsBuilder.newInstance();
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).login(null)).withRel("login"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).registerAdmin(null, dummyUriBuilder)).withRel("register admin"));
        return ResponseEntity.created(uri).body(userDetailsDto);
    }

    @PostMapping("/v1/registerAdmin")
    @ApiResponse(responseCode = "201", description = "Registro bem sucedido")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    @Transactional
    public ResponseEntity<UserDetailsDto> registerAdmin(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userService.convertDtoToEntity(userRegistrationDto);
        userService.registerUserAdmin(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
        UserDetailsDto userDetailsDto = new UserDetailsDto(user);
        UriComponentsBuilder dummyUriBuilder = UriComponentsBuilder.newInstance();
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).login(null)).withRel("login"));
        userDetailsDto.add(linkTo(methodOn(AuthenticationController.class).register(null, dummyUriBuilder)).withRel("register"));
        return ResponseEntity.created(uri).body(userDetailsDto);
    }
}
