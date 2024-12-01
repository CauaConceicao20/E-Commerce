package com.compass.e_commerce.dto.role;

import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDetailsDto extends RepresentationModel<RoleDetailsDto> {

    private RoleNameEnum name;

    public RoleDetailsDto(Role role) {
        this.name = role.getName();
    }

}
