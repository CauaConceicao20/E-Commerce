package com.compass.e_commerce.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDetailsDto extends RepresentationModel<UserLoginDetailsDto> {

    private String token;
}
