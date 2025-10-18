package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.Validation;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Optional;

public class PersonName {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 70;

    private final String value;

    private PersonName(String value) {
        this.value = validate(value);
    }

    public static PersonName of(String value) {
        return new PersonName(value);
    }

    public static Optional<PersonName> ofNullable(String value) {
        return StringUtils.isBlank(value)
                ? Optional.empty()
                : Optional.of(new PersonName(value));
    }

    private static String validate(String value) {
        var trimmedValue = Validation.required(value, "O nome é requerido.");
        if (trimmedValue.length() < MIN_LENGTH || trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidPersonNameException(
                    "O nome deve ter de %d a %d caracteres."
                    .formatted(MIN_LENGTH, MAX_LENGTH)
            );
        }
        return trimmedValue;
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
        PersonName that = (PersonName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}