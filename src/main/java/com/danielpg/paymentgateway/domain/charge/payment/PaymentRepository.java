package com.danielpg.paymentgateway.domain.charge.payment;


import com.danielpg.paymentgateway.domain.charge.ChargeId;

import java.util.Optional;

public interface PaymentRepository {
    boolean exists(ChargeId chargeId);
    Optional<Payment> get(ChargeId chargeId);
    Optional<Payment> get(PaymentId id);
    Payment getOrThrow(PaymentId id);
    void save(Payment payment);
}
