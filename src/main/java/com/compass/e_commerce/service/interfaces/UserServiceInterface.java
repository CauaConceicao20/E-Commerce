package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UserServiceInterface {
    User registerUser(User user);
    User registerUserAdmin(User user);
    List<User> getAll();
    User getById(Long id);
    User updateByAdmin(Long id, UserUpdateDto userUpdateDto);
    User updateMyProfile(UserUpdateDto userUpdateDto);

    void logicUpdate(User user, UserUpdateDto userUpdateDto);

    void delete(Long id);
}
