package com.compass.e_commerce.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorDetails {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime time;

    public ErrorDetails(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.time = LocalDateTime.now();
    }
/*
   public ErrorDetails(FieldError erro) {
        this.message = erro.getDefaultMessage();
        this.detail = erro.getField();
    }
    public ErrorDetails(String message, String detail) {
        this.message = message;
        this.detail = detail;
    }
    public ErrorDetails(String message) {
        this.message = message;
    }

 */
}
