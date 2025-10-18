package com.danielpg.paymentgateway.domain.charge.payment;

public class PaymentNotAuthorizedException extends RuntimeException {

    public PaymentNotAuthorizedException() {
        super("Pagamento não autorizado.");
    }
}
