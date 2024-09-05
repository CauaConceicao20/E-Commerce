package com.compass.e_commerce.exception.personalized;

public class SaleAlreadyConfirmedException extends RuntimeException {
    public SaleAlreadyConfirmedException(String message) {
        super(message);
    }
}
