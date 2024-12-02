
package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.user.UserRegistrationDto;
import jakarta.persistence.*;
import lombok.*;
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
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sale> sales = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private boolean active;

    public User(UserRegistrationDto userRegistrationDto, Cart cart) {
        this.login = userRegistrationDto.login();
        this.password = userRegistrationDto.password();
        this.email = userRegistrationDto.email();
        this.cart = cart;
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

