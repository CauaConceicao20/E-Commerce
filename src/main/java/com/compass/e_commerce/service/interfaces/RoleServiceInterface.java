package com.compass.e_commerce.service.interfaces;

import com.compass.e_commerce.model.Role;

import java.util.List;

public interface RoleServiceInterface {
    Role create(Role role);

    List<Role> getAll();
}
