package com.compass.e_commerce.dto.role;

import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import org.springframework.hateoas.RepresentationModel;

public class RoleListDto extends RepresentationModel<RoleListDto> {
    private RoleNameEnum role;
    public RoleListDto(Role role) {
        this.role = role.getName();
    }
}
