package com.danielpg.paymentgateway.application.auth;

public class InvalidCredentialsException extends RuntimeException {

    public  InvalidCredentialsException() {
        super("Usuário ou senha inválida.");
    }
}
