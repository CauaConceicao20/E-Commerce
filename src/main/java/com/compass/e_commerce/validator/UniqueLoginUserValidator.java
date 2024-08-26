package com.compass.e_commerce.validator;

import com.compass.e_commerce.annotations.UniqueLoginUser;
import com.compass.e_commerce.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueLoginUserValidator implements ConstraintValidator<UniqueLoginUser,String> {

    @Autowired
    private UserRepository userRepository;

    public UniqueLoginUserValidator() {

    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if(login == null || userRepository == null) {
            return true;
        }
        return !userRepository.existsByLogin(login);
    }
}
