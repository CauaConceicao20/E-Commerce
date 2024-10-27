package com.compass.e_commerce.exception.personalized;

public class AccessRestrictException extends RuntimeException{
    public  AccessRestrictException(String message) {
        super(message);
    }
}
