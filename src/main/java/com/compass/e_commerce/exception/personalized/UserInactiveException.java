package com.compass.e_commerce.exception.personalized;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super(message);
    }
}