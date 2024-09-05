package com.compass.e_commerce.exception.personalized;

public class ExceededStockException extends RuntimeException {
    public ExceededStockException(String message) {
        super(message);
    }
}
