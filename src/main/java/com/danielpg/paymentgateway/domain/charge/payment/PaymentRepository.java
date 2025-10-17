package com.danielpg.paymentgateway.domain.charge.payment;


import com.danielpg.paymentgateway.domain.charge.ChargeId;

public interface PaymentRepository {
    boolean exists(ChargeId charId);
    void save(Payment payment);
}
