package com.danielpg.paymentgateway.domain.charge;

import java.util.Optional;

public interface ChargeRepository {
    Optional<Charge> get(ChargeId id);
    Charge getOrThrow(ChargeId id);
    void save(Charge charge);
}
