package com.danielpg.paymentgateway.domain.user;

public class InsufficientBalanceException extends IllegalStateException {

    public InsufficientBalanceException() {
        super("Saldo insuficiente.");
    }
}
