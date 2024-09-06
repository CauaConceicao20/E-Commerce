package com.compass.e_commerce.repository;

import com.compass.e_commerce.model.User;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.id = :id AND r.name = :roleName")
    Optional<User> findByIdAdmin(@Param("id") Long id, @Param("roleName") RoleNameEnum roleName);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE u.id = :id AND r.name = :roleName AND SIZE(u.roles) = 1")
    Optional<User> findByIdUser(@Param("id") Long id, @Param("roleName") RoleNameEnum roleName);
    Optional<User> findByLogin(String login);
    List<User> findByActiveTrue();
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
