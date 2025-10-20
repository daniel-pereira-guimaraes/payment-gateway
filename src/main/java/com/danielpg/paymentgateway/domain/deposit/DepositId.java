package com.danielpg.paymentgateway.domain.deposit;

import com.danielpg.paymentgateway.domain.shared.SurrogateId;

import java.util.Optional;

public class DepositId extends SurrogateId {

    protected DepositId(Long value) {
        super(value);
    }

    public static DepositId of(Long value) {
        return new DepositId(value);
    }

    public static Optional<DepositId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new DepositId(value));
    }
}
