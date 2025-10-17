package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.SurrogateId;

import java.util.Optional;

public class UserId extends SurrogateId {

    protected UserId(Long value) {
        super(value);
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }

    public static Optional<UserId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new UserId(value));
    }
}
