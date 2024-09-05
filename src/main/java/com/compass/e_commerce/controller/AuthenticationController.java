package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.TokenService;
import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.dto.user.UserAuthenticationDto;
import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserLoginDetailsDto;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.exception.personalized.UserInactiveException;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginDetailsDto> login(@RequestBody @Valid UserAuthenticationDto authenticationDto) {
        var user = userService.findByLogin(authenticationDto.login());
        if (!user.getActive()) {
            throw new UserInactiveException("Usuário está inativo");
        }
        var loginPassword = new UsernamePasswordAuthenticationToken(authenticationDto.login(), authenticationDto.password());
        var auth = this.authenticationManager.authenticate(loginPassword);

        var token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());

        return ResponseEntity.ok(new UserLoginDetailsDto(token));
    }

    @PostMapping("/registerUser")
    public ResponseEntity<UserDetailsDto> create(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userService.convertDtoToEntity(userRegistrationDto);
        userService.registerUser(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetailsDto((user)));
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<UserDetailsDto> createAdmin(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userService.convertDtoToEntity(userRegistrationDto);
        userService.registerUserAdmin(user);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetailsDto((user)));
    }
}
