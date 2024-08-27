
package com.compass.e_commerce.model.user;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import com.compass.e_commerce.model.role.Role;
import com.compass.e_commerce.model.role.RoleName;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name="tb_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(UserRegistrationDto userRegistrationDto) {
        this.login = userRegistrationDto.login();
        this.password = userRegistrationDto.password();
        this.email = userRegistrationDto.email();
    }
}

