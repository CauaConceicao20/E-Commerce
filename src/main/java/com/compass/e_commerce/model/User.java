
package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name="tb_users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true, nullable = false, length = 14)
    @CPF
    private String cpf;

    @Column(nullable = false, length = 11)
    private String phone;

    @Embedded
    private Address address;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private boolean active;

    public User(UserRegistrationDto userRegistrationDto) {
        this.username = userRegistrationDto.username();
        this.password = userRegistrationDto.password();
        this.email = userRegistrationDto.email();
        this.cpf = userRegistrationDto.cpf();
        this.phone = userRegistrationDto.phone();
        this.address = new Address(userRegistrationDto.address());
        this.active = true;

    }
    @CacheEvict(value = "users", allEntries = true)
    public void isActive() {
        this.active = true;
    }

    @CacheEvict(value = "users", allEntries = true)
    public void isInactive() {
        this.active = false;
    }

    public boolean getActive() {
        return this.active;
    }
}

