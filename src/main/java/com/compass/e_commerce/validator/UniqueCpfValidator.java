package com.compass.e_commerce.validator;

import com.compass.e_commerce.annotations.UniqueCpf;
import com.compass.e_commerce.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UniqueCpfValidator implements ConstraintValidator<UniqueCpf, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if(cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        return !userRepository.existsByCpf(cpf);
    }


}
