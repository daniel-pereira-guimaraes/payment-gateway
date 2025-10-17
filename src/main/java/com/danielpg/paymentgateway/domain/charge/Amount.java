package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.Validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class Amount {

    private static final int SCALE = 2;
    private static final BigDecimal MIN_VALUE = new BigDecimal("0.01");
    private static final BigDecimal MAX_VALUE = new BigDecimal("999999999.99");

    private final BigDecimal value;

    private Amount(BigDecimal value) {
        this.value = validate(value);
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
        return other instanceof Amount otherAmount
                && value.compareTo(otherAmount.value) == 0;
    }

    private static BigDecimal validate(BigDecimal value) {
        var scaledValue = round(Validation.required(value, "O valor é requerido."));
        if (value.compareTo(MIN_VALUE) < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("O valor deve ser de %s a %s.".formatted(MIN_VALUE, MAX_VALUE));
        }
        return scaledValue;
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_EVEN);
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
