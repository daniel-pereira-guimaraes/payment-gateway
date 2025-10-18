package com.danielpg.paymentgateway.domain.shared;

public abstract class AbstractNotFoundException extends RuntimeException {

    protected AbstractNotFoundException(String message) {
        super(message);
    }
}
