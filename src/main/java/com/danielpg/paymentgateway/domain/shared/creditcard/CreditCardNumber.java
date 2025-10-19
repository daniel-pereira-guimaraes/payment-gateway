package com.danielpg.paymentgateway.domain.shared.creditcard;

import io.micrometer.common.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class CreditCardNumber {

    private static final Pattern PATTERN = Pattern.compile("^\\d{13,19}$");

    private final String value;

    private CreditCardNumber(String value) {
        this.value = validate(value);
    }

    public static CreditCardNumber of(String value) {
        return new CreditCardNumber(value);
    }

    public static Optional<CreditCardNumber> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new CreditCardNumber(value));
    }

    private static String validate(String value) {
        var trimmed = Objects.requireNonNull(value).trim();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Número de cartão de crédito inválido.");
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
        return Objects.equals(value, ((CreditCardNumber) o).value);
    }

}
