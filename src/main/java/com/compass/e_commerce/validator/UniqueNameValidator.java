package com.compass.e_commerce.validator;

import com.compass.e_commerce.annotations.UniqueNameRole;
import com.compass.e_commerce.model.enums.RoleNameEnum;
import com.compass.e_commerce.repository.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueNameValidator implements ConstraintValidator<UniqueNameRole, RoleNameEnum> {

    @Autowired
    private RoleRepository roleRepository;

    public UniqueNameValidator() {
    }

    @Override
    public boolean isValid(RoleNameEnum roleNameEnum, ConstraintValidatorContext context) {
        if(roleNameEnum == null || roleRepository == null) {
            return true;
        }
        return !roleRepository.existsByName(roleNameEnum);
    }
}
