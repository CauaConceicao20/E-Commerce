package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.model.user.User;
import com.compass.e_commerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDetailsDto> create(@RequestBody @Valid UserRegistrationDto userRegistrationDto, UriComponentsBuilder uriBuilder) {
        User user = userService.convertDtoToEntity(userRegistrationDto);
        userService.create(user, userRegistrationDto.rolesDto().roles());

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserDetailsDto((user)));
    }
}
