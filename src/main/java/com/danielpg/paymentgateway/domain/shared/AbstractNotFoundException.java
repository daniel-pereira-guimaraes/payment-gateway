package com.danielpg.paymentgateway.domain.shared;

public abstract class AbstractNotFoundException extends IllegalArgumentException {

    protected AbstractNotFoundException(String message) {
        super(message);
    }
}
