package com.danielpg.paymentgateway.domain.user;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Saldo insuficiente.");
    }
}
