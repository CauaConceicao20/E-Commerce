package com.compass.e_commerce.exception;

public class SaleAlreadyConfirmedException extends RuntimeException {
    public SaleAlreadyConfirmedException(String message) {
        super(message);
    }
}
