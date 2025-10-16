package com.danielpg.paymentgateway.domain.user;

public class InvalidPersonNameException extends IllegalArgumentException {

    public InvalidPersonNameException(String message) {
        super(message);
    }

}
