package com.compass.e_commerce.validator;

import com.compass.e_commerce.annotations.UniqueEmail;
import com.compass.e_commerce.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    public UniqueEmailValidator(){

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email == null || userRepository == null) {
            return true;
        }
        return !userRepository.existsByEmail(email);
    }
}
