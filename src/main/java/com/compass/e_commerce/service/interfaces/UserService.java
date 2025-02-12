package com.compass.e_commerce.service.interfaces;

import java.util.List;

public interface UserService<T, D> {

    T registerUser(T entity);

    T registerUserAdmin(T entity);

    T findByEmail(String email);

    T findByLogin(String login);

    T findByCpf(String cpf);

    void changePassword(T entity, String newPassword);

    T updateMyProfile(D dto);

    void logicUpdate(T entity, D dto);

}
