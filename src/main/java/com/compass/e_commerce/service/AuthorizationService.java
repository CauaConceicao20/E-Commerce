package com.compass.e_commerce.service;

import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.UserRepository;
import com.compass.e_commerce.service.interfaces.AuthorizationServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService, AuthorizationServiceImp {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + login));
        return new UserDetailsImpl(user);
    }
}
