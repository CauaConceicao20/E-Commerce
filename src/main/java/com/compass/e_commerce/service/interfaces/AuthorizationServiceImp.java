package com.compass.e_commerce.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthorizationServiceImp {
    UserDetails loadUserByUsername(String login);
}
