package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.AbstractAlreadyExistsException;

public class UserAlreadyExistsException extends AbstractAlreadyExistsException {

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
