package com.danielpg.paymentgateway.domain.shared;

public class AbstractAlreadyExistsException extends IllegalStateException {

    public AbstractAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
