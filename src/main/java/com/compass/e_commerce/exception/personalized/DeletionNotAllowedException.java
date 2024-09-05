package com.compass.e_commerce.exception.personalized;

public class  DeletionNotAllowedException extends RuntimeException {
    public  DeletionNotAllowedException(String message) {
        super(message);
    }
}
