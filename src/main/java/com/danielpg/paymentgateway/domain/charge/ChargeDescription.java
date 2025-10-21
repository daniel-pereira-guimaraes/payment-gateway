package com.danielpg.paymentgateway.domain.charge;

import io.micrometer.common.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class ChargeDescription {

    private static final int MAX_LENGTH = 70;

    private final String value;

    private ChargeDescription(String value) {
        this.value = validate(value);
    }

    public static ChargeDescription of(String value) {
        return new ChargeDescription(value);
    }

    public static Optional<ChargeDescription> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new ChargeDescription(value));
    }

    public String value() {
        return value;
    }

    private String validate(String value) {
        var trimmed = Objects.requireNonNull(value).trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "A descrição deve ter no máximo %d caracteres.".formatted(MAX_LENGTH)
            );
        }
        return trimmed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ChargeDescription that = (ChargeDescription) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
