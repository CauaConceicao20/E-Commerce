package com.compass.e_commerce.exception;

public class  DeletionNotAllowedException extends RuntimeException {
    public  DeletionNotAllowedException(String message) {
        super(message);
    }
}
