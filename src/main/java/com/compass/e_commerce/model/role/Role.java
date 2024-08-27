package com.compass.e_commerce.model.role;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "tb_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name;

    public Role(RoleRegistrationDto roleRegistrationDto) {
        this.name = roleRegistrationDto.name();
    }

}
