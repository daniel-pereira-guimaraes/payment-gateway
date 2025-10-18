package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.SurrogateId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
