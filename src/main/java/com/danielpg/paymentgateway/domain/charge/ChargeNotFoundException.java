package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.AbstractNotFoundException;

public class ChargeNotFoundException extends AbstractNotFoundException {

    public ChargeNotFoundException(ChargeId id) {
        super("Cobrança não encontrada: " + id.value());
    }
}
