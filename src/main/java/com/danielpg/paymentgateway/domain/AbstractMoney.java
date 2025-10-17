package com.danielpg.paymentgateway.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class AbstractMoney implements Comparable<AbstractMoney> {

    private static final int SCALE = 2;

    private final BigDecimal value;

    protected AbstractMoney(BigDecimal value, BigDecimal minValue, BigDecimal maxValue) {
        this.value = validate(value, minValue, maxValue);
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AbstractMoney otherMoney
                && value.compareTo(otherMoney.value) == 0;
    }

    private static BigDecimal validate(BigDecimal value, BigDecimal minValue, BigDecimal maxValue) {
        var scaledValue = round(Validation.required(value, "O valor é requerido."));
        if (value.compareTo(minValue) < 0 || value.compareTo(maxValue) > 0) {
            throw new IllegalArgumentException("O valor deve ser de %s a %s.".formatted(minValue, maxValue));
        }
        return scaledValue;
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_EVEN);
    }

    @Override
    public int compareTo(AbstractMoney other) {
        return value.compareTo(other.value);
    }

}
