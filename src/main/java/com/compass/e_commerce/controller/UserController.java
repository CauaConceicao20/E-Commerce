package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserListDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<UserListDto>> listUsers() {
        var userList = userService.findAll().stream().map(UserListDto::new).toList();
        return ResponseEntity.ok().body(userList);
    }

    @PutMapping("/update")
    public ResponseEntity<UserDetailsDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user =  userService.update(userUpdateDto);
        return ResponseEntity.ok().body(new UserDetailsDto(user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
