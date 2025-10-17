package com.danielpg.paymentgateway.domain;

import java.math.BigDecimal;
import java.util.Optional;

public class Amount extends AbstractMoney {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.01");
    private static final BigDecimal MAX_VALUE = new BigDecimal("999999999.99");

    protected Amount(BigDecimal value) {
        super(value, MIN_VALUE, MAX_VALUE);
    }

    public Amount add(Amount amount) {
        return new Amount(value().add(amount.value()));
    }

    public Amount subtract(Amount amount) {
        return new Amount(value().subtract(amount.value()));
    }

    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }

    public static Optional<Amount> ofNullable(BigDecimal value) {
        return value == null
                ? Optional.empty()
                : Optional.of(new Amount(value));
    }
}
