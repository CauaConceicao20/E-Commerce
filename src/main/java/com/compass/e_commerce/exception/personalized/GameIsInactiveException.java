package com.compass.e_commerce.exception.personalized;

public class GameIsInactiveException extends RuntimeException {
    public GameIsInactiveException(String message) {
        super(message);
    }
}
