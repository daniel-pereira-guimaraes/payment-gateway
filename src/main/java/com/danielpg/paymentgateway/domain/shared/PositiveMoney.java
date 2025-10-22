package com.danielpg.paymentgateway.domain.shared;

import java.math.BigDecimal;
import java.util.Optional;

public class PositiveMoney extends AbstractMoney {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.01");
    private static final BigDecimal MAX_VALUE = new BigDecimal("999999999.99");

    protected PositiveMoney(BigDecimal value) {
        super(value, MIN_VALUE, MAX_VALUE);
    }

    public PositiveMoney add(PositiveMoney amount) {
        return new PositiveMoney(value().add(amount.value()));
    }

    public PositiveMoney subtract(PositiveMoney amount) {
        return new PositiveMoney(value().subtract(amount.value()));
    }

    public static PositiveMoney of(BigDecimal value) {
        return new PositiveMoney(value);
    }

    public static Optional<PositiveMoney> ofNullable(BigDecimal value) {
        return value == null
                ? Optional.empty()
                : Optional.of(new PositiveMoney(value));
    }

}
