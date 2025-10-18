package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.Validation;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Optional;

public class HashedPassword {

    private final String hash;

    private HashedPassword(String hash) {
        this.hash = Validation.required(hash, "O hash da senha é requerido.");
    }

    public static HashedPassword of(String hash) {
        return new HashedPassword(hash);
    }

    public static Optional<HashedPassword> ofNullable(String hash) {
        return StringUtils.isBlank(hash)
                ? Optional.empty()
                : Optional.of(new HashedPassword(hash));
    }

    public String hash() {
        return hash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashedPassword that = (HashedPassword) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
