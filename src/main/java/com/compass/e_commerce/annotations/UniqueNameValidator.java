package com.compass.e_commerce.annotations;

import com.compass.e_commerce.model.role.RoleName;
import com.compass.e_commerce.repository.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueNameValidator implements ConstraintValidator<UniqueName, RoleName> {

    @Autowired
    private RoleRepository roleRepository;

    public UniqueNameValidator() {
    }

    @Override
    public boolean isValid(RoleName roleName, ConstraintValidatorContext context) {
        if(roleName == null || roleRepository == null) {
            return true;
        }
        return !roleRepository.existsByName(roleName);
    }
}
