package com.compass.e_commerce.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNameValidator.class)
public @interface UniqueName {

        String message() default "Name must be unique";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
}
