package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.user.User;

public record UserDetailsDto(Long id, String login, String password) {
    public UserDetailsDto(User user) {
        this(user.getId(),
             user.getLogin(),
             user.getPassword());
    }
}
