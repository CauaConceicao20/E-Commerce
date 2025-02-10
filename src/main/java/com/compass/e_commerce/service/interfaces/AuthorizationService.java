package com.compass.e_commerce.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthorizationService<T> {
    T loadUserByUsername(String login);

    Long getAuthenticatedUserId();
}
