package com.compass.e_commerce.annotations;

import com.compass.e_commerce.validator.UniqueLoginUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueLoginUserValidator.class)
public @interface UniqueLoginUser {
    String message() default "O nome de username deve ser único; já existe um usuário com este nome.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
