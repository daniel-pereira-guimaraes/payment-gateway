package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.shared.AbstractNotFoundException;

public class PaymentNotFoundException extends AbstractNotFoundException {

    public PaymentNotFoundException(PaymentId id) {
        super("Pagamento não encontrado: " + id);
    }
}
