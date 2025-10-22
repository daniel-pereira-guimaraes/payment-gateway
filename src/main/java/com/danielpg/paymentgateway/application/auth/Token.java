package com.danielpg.paymentgateway.application.auth;

import com.danielpg.paymentgateway.domain.shared.AppClock;
import com.danielpg.paymentgateway.domain.user.User;

import java.util.Objects;

public class Token {

    private final String rawToken;
    private final User user;
    private final Long expiration;
    private final AppClock clock;

    private Token(Builder builder) {
        this.rawToken = Objects.requireNonNull(builder.rawToken);
        this.user = Objects.requireNonNull(builder.user);
        this.expiration = Objects.requireNonNull(builder.expiration);
        this.clock = Objects.requireNonNull(builder.clock);
    }

    public String rawToken() {
        return rawToken;
    }

    public User user() {
        return user;
    }

    public Long expiration() {
        return expiration;
    }

    public boolean isExpired() {
        return expiration < clock.now().value();
    }

    @Override
    public int hashCode() {
        return Objects.hash(rawToken, user, expiration);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Token otherToken
                && equalsCasted(otherToken);
    }

    private boolean equalsCasted(Token other) {
        return Objects.equals(rawToken, other.rawToken)
                && Objects.equals(user, other.user)
                && Objects.equals(expiration, other.expiration);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String rawToken;
        private User user;
        private Long expiration;
        private AppClock clock;

        private Builder() {
        }

        public Builder withRawToken(String rawToken) {
            this.rawToken = rawToken;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withExpiration(Long expiration) {
            this.expiration = expiration;
            return this;
        }

        public Builder withClock(AppClock clock) {
            this.clock = clock;
            return this;
        }

        public Token build() {
            return new Token(this);
        }
    }
}
