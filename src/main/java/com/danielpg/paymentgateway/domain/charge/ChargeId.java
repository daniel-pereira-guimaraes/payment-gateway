package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.shared.SurrogateId;

import java.util.Optional;

public class ChargeId extends SurrogateId {

    protected ChargeId(Long value) {
        super(value);
    }

    public static ChargeId of(Long value) {
        return new ChargeId(value);
    }

    public static Optional<ChargeId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new ChargeId(value));
    }
}
