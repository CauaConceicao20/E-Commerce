package com.compass.e_commerce.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

@Getter
@Setter
public class ErroDetails {
     private String message;
     private String detail;

   public ErroDetails(FieldError erro) {
        this.message = erro.getDefaultMessage();
        this.detail = erro.getField();
    }
    public ErroDetails(String message, String detail) {
        this.message = message;
        this.detail = detail;
    }
    public ErroDetails(String message) {
        this.message = message;
    }
}
