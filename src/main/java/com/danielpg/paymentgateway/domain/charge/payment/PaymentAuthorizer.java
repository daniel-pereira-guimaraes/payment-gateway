package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;

public interface PaymentAuthorizer {
    void authorizePayment(Charge charge);
}
