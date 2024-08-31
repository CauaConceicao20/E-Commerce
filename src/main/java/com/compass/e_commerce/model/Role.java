package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.role.RoleRegistrationDto;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleNameEnum name;

    public Role(RoleRegistrationDto roleRegistrationDto) {
        this.name = roleRegistrationDto.name();
    }

}
