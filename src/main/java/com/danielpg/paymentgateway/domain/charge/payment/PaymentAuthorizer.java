package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;

public interface PaymentAuthorizer {
    void authorizePayment(Charge charge, CreditCard creditCard);
}
