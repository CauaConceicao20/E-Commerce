package com.compass.e_commerce.service;

import com.compass.e_commerce.config.security.UserDetailsImpl;
import com.compass.e_commerce.model.User;
import com.compass.e_commerce.repository.UserRepository;
import com.compass.e_commerce.service.interfaces.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements UserDetailsService, AuthorizationService<UserDetails> {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        User user = userRepository.findByUsername(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o username: " + login));
        return new UserDetailsImpl(user);
    }

    @Override
    public Long getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            var userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (userDetails != null) {
                return userDetails.getId();
            }
        }
        throw new UsernameNotFoundException("User não encontrado");
    }
}
