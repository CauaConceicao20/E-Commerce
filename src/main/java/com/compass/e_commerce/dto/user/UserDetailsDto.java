package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.User;

public record UserDetailsDto(Long id, String login, String email) {
    public UserDetailsDto(User user) {
        this(user.getId(),
             user.getLogin(),
             user.getEmail());
    }
}
