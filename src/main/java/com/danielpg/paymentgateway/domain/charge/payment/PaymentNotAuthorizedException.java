package com.danielpg.paymentgateway.domain.charge.payment;

public class PaymentNotAuthorizedException extends IllegalStateException {

    public PaymentNotAuthorizedException() {
        super("Pagamento não autorizado.");
    }
}
