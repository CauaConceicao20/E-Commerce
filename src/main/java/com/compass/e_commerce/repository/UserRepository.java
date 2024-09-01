package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
