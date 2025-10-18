package com.danielpg.paymentgateway.application.auth;

public class InvalidCredentialsException extends IllegalArgumentException {

    public  InvalidCredentialsException() {
        super("Usuário ou senha inválida.");
    }
}
