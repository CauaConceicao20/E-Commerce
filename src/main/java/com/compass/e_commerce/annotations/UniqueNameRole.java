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

        String message() default "The role name must be unique; a role with this name already exists.";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
