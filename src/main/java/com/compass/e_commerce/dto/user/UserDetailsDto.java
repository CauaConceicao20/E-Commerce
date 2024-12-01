package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto extends RepresentationModel<UserDetailsDto> {
    private Long id;
    private String login;
    private String email;

    public UserDetailsDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.email = user.getEmail();
    }
}
