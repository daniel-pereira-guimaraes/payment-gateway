package com.danielpg.paymentgateway.domain.user;

import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Optional;

public class Cpf {

    private static final int START_WEIGHT = 2;

    private final String value;

    private Cpf(String value) {
        this.value = validate(value);
    }

    public static Cpf of(String value) {
        return new Cpf(value);
    }

    public static Optional<Cpf> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new Cpf(value));
    }

    public String value() {
        return value;
    }

    private String validate(String value) {
        if (!isCpf(value)) {
            throw new InvalidCpfException(value);
        }
        return value;
    }

    private static boolean isCpf(String value) {
        return value != null
                && value.length() == 11
                && validateDigitAt(value, 9, 10)
                && validateDigitAt(value, 10, 11)
                && hasVariedCharacters(value);
    }

    private static boolean validateDigitAt(String value, int position, int endWeight) {
        int expectedDigit = Character.getNumericValue(value.charAt(position));
        int calculatedDigit = modulo11Checksum(value.substring(0, position), endWeight);
        return expectedDigit == calculatedDigit;
    }

    private static int modulo11Checksum(String value, int endWeight) {
        int sum = 0;
        int weight = START_WEIGHT;
        int length = value.length();
        for (int i = length - 1; i >= 0; i--) {
            sum += Character.getNumericValue(value.charAt(i)) * weight++;
            if (weight > endWeight) {
                weight = START_WEIGHT;
            }
        }
        int remainder = sum % 11;
        return remainder < START_WEIGHT ? 0 : 11 - remainder;
    }

    private static boolean hasVariedCharacters(String value) {
        return value != null
                && value.chars().distinct().count() > 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cpf cpf = (Cpf) o;
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
