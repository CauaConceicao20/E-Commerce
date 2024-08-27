package com.compass.e_commerce.annotations;

import com.compass.e_commerce.validator.UniqueEmailValidator;
import com.compass.e_commerce.validator.UniqueLoginUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "The email must be unique; an account with this email already exists.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
