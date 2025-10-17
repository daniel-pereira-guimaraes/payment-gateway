package com.danielpg.paymentgateway.domain.charge;

public interface ChargeRepository {
    Charge getOrThrow(ChargeId id);
    void save(Charge charge);
}
