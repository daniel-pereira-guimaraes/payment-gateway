package com.danielpg.paymentgateway.domain;

public abstract class AbstractNotFoundException extends RuntimeException {

    protected AbstractNotFoundException(String message) {
        super(message);
    }
}
