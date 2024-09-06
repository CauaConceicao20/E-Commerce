package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.RoleNameEnum;

import java.util.List;
import java.util.stream.Collectors;

public record UserListDto(
        Long id,
        String login,
        String email,
        List<RoleNameEnum> roles
) {
    public UserListDto(User user) {
        this(user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()));
    }
}
