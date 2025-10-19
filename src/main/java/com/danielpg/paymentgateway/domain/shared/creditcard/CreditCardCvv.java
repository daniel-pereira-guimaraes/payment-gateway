package com.danielpg.paymentgateway.domain.shared.creditcard;

import io.micrometer.common.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public final class CreditCardCvv {

    private static final Pattern PATTERN = Pattern.compile("^\\d{3,4}$");
    private static final String INVALID_MESSAGE = "CVV inválido.";

    private final String value;

    private CreditCardCvv(String value) {
        this.value = validate(value);
    }

    public static CreditCardCvv of(String value) {
        return new CreditCardCvv(value);
    }

    public static Optional<CreditCardCvv> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new CreditCardCvv(value));
    }

    private static String validate(String value) {
        var trimmed = Objects.requireNonNull(value).trim();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(INVALID_MESSAGE);
        }
        return trimmed;
    }

    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(value, ((CreditCardCvv) o).value);
    }
}
