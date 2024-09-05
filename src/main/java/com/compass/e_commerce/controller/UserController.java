package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.game.GameDetailsDto;
import com.compass.e_commerce.dto.user.AdminUpdateDto;
import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserListDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<UserListDto>> listUsers() {
        var userList = userService.findAll().stream().map(UserListDto::new).toList();
        return ResponseEntity.ok().body(userList);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserDetailsDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user =  userService.updateUser(userUpdateDto);
        return ResponseEntity.ok().body(new UserDetailsDto(user));
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<UserDetailsDto> updateAdmin(@RequestBody @Valid AdminUpdateDto adminUpdateDto) {
        User user =  userService.updateAdmin(adminUpdateDto);
        return ResponseEntity.ok().body(new UserDetailsDto(user));
    }

    @PutMapping("/isActive/{id}")
    @Transactional
    //@CacheEvict(value = "games", allEntries = true)
    public ResponseEntity<Void> activeGame(@PathVariable Long id) {
        User user = userService.findById(id);
        user.isActive();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/isInactive/{id}")
    @Transactional
    //@CacheEvict(value = "games", allEntries = true)
    public ResponseEntity<Void> inactiveGame(@PathVariable Long id) {
        User user = userService.findById(id);
        user.isInactive();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
