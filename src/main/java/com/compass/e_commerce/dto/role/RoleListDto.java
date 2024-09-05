package com.compass.e_commerce.dto.role;

import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;

public record RoleListDto(
        RoleNameEnum role
) {
    public RoleListDto(Role role) {
        this(role.getName());
    }
}
