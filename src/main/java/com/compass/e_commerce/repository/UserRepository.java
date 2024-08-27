package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.role.Role;
import com.compass.e_commerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}
