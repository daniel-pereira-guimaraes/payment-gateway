package com.danielpg.paymentgateway.domain.user;

public class InvalidPasswordException extends IllegalArgumentException {

    public InvalidPasswordException(String message) {
        super(message);
    }

}
