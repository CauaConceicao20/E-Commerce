package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.model.User;

public record UserListDto(
        Long id,
        String login,
        String email
) {
    public UserListDto(User user) {
        this(user.getId(),
                user.getLogin(),
                user.getEmail());
    }
}
