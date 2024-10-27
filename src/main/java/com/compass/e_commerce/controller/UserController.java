package com.compass.e_commerce.controller;

import com.compass.e_commerce.config.security.SecurityConfigurations;
import com.compass.e_commerce.dto.user.UserDetailsDto;
import com.compass.e_commerce.dto.user.UserListDto;
import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/getAll")
    @Operation(summary = "List Users")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<List<UserListDto>> listUsers() {
        var userList = userService.getAll().stream().map(UserListDto::new).toList();
        return ResponseEntity.ok().body(userList);
    }

    @PutMapping("/v1/updateAdmin/{id}")
    @Operation(summary = "Update Admin")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "409", description = "usuario está inativado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<UserDetailsDto> updateByAdmin(@PathVariable long id , @RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user =  userService.updateByAdmin(id,userUpdateDto);
        return ResponseEntity.ok().body(new UserDetailsDto(user));
    }

    @PutMapping("/v1/updateProfile")
    @Operation(summary = "Update my profile")
    @ApiResponse(responseCode = "200", description = "Listagem bem sucedida")
    @ApiResponse(responseCode = "400", description = "Dado incorretos")
    @ApiResponse(responseCode = "409", description = "usuario está inativado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<UserDetailsDto> updateMyProfile(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        User user =  userService.updateMyProfile(userUpdateDto);
        return ResponseEntity.ok().body(new UserDetailsDto(user));
    }

    @PutMapping("/v1/isActive/{id}")
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    @Operation(summary = "Active User")
    @ApiResponse(responseCode = "204", description = "Ativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> activeUser(@PathVariable Long id) {
        User user = userService.getById(id);
        user.isActive();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/v1/isInactive/{id}")
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    @Operation(summary = "Inactive User")
    @ApiResponse(responseCode = "204", description = "Inativação bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> inactiveUser(@PathVariable Long id) {
        User user = userService.getById(id);
        user.isInactive();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/delete/{id}")
    @Operation(summary = "Delete User")
    @ApiResponse(responseCode = "204", description = "Deleção bem sucedida")
    @ApiResponse(responseCode = "404", description = "User não encontrado")
    @ApiResponse(responseCode = "409", description = "User está inativado")
    @ApiResponse(responseCode = "409", description = "User está associado a vendas")
    @ApiResponse(responseCode = "503", description = "Falha de conexão com Redis")
    @ApiResponse(responseCode = "500", description = "Erro no Servidor")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
