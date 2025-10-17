package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.SurrogateId;

import java.util.Optional;

public class PaymentId extends SurrogateId {

    protected PaymentId(Long value) {
        super(value);
    }

    public static PaymentId of(Long value) {
        return new PaymentId(value);
    }

    public static Optional<PaymentId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new PaymentId(value));
    }
}
