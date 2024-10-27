package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.role.RoleListDto;
import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Tag(name = "Role")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/v1/create")
    @Operation(summary = "Create Role")
    @ApiResponse(responseCode = "201", description = "Criação de Role bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dados invalidos")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Role> create(@RequestBody @Valid RoleRegistrationDto roleRegistrationDto) {
        Role role = roleService.convertDtoToEntity(roleRegistrationDto);
        roleService.create(role);

        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
    @GetMapping("/v1/getAll")
    @Operation(summary = "List Roles")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<RoleListDto>> list() {
        var roles = roleService.getAll().stream().map(RoleListDto::new).toList();
        return ResponseEntity.ok().body(roles);
    }
}
