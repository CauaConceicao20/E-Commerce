package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.dto.user.UserUpdateDto;
import com.compass.e_commerce.model.User;

import java.util.List;

public interface UserServiceInterface {
    User registerUser(User user);
    User registerUserAdmin(User user);
    List<User> getAll();
    User getById(Long id);
    User update(UserUpdateDto userUpdateDto);
    void delete(Long id);
}
