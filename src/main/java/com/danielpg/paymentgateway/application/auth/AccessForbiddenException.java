package com.danielpg.paymentgateway.application.auth;

public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException() {
        super("Acesso negado!");
    }
}
