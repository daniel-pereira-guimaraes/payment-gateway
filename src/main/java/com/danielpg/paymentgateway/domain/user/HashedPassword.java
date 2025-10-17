package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.Validation;
import io.micrometer.common.util.StringUtils;

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
}
