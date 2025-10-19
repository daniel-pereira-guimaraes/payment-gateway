package com.danielpg.paymentgateway.infrastructure.integration;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import org.springframework.stereotype.Component;

@Component
public class PaymentAuthorizerImpl implements PaymentAuthorizer {

    @Override
    public void authorizePayment(Charge charge) {
        //TODO: Implementar!
    }
}
