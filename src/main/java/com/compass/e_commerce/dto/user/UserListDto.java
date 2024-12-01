package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.Role;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto extends RepresentationModel<UserListDto> {

    private Long id;
    private String login;
    private String email;
    private List<RoleNameEnum> roles;

    public UserListDto(User user) {
        this(
                user.getId(),
                user.getLogin(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
        );
    }
}
