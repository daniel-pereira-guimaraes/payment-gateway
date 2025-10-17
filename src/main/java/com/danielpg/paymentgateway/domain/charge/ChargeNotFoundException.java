package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.AbstractNotFoundException;

public class ChargeNotFoundException extends AbstractNotFoundException {

    protected ChargeNotFoundException(Charge charge) {
        super("Cobrança não encontrada: " + charge.id().value());
    }
}
