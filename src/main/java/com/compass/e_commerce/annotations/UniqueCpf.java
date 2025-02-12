package com.compass.e_commerce.annotations;

import com.compass.e_commerce.validator.UniqueCpfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueCpfValidator.class)
public @interface UniqueCpf {

    String message() default "O cpf deve ser único; já existe uma conta com este cpf.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
