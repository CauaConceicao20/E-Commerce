package com.compass.e_commerce.annotations;

import com.compass.e_commerce.validator.UniqueNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNameValidator.class)
public @interface UniqueNameRole {

    String message() default "O nome da Role deve ser único; já existe uma função com este nome.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
