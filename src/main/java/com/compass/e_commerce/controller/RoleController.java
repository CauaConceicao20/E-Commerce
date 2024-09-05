package com.compass.e_commerce.controller;

import com.compass.e_commerce.dto.role.RoleListDto;
import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<Role> create(@RequestBody @Valid RoleRegistrationDto roleRegistrationDto) {
        Role role = roleService.convertDtoToEntity(roleRegistrationDto);
        roleService.create(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoleListDto>> listRole() {
        var roles = roleService.list().stream().map(RoleListDto::new).toList();
        return ResponseEntity.ok().body(roles);
    }
}
