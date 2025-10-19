package com.danielpg.paymentgateway.domain.charge.payment;

public class PaymentNotAuthorizedException extends IllegalStateException {

    public PaymentNotAuthorizedException(String message) {
        super(message);
    }
}
