package com.danielpg.paymentgateway.domain.deposit;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.shared.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.util.Objects;

public class Deposit {

    private DepositId id;
    private final UserId userId;
    private final PositiveMoney amount;
    private final TimeMillis createdAt;

    private Deposit(Builder builder) {
        this.id = builder.id;
        this.userId = Validation.required(builder.userId, "O usuário é requerido.");
        this.amount = Validation.required(builder.amount, "O valor é requerido.");
        this.createdAt = Validation.required(builder.createdAt, "A data/hora do depósito é requerida;");
    }

    public DepositId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public PositiveMoney amount() {
        return amount;
    }

    public TimeMillis createdAt() {
        return createdAt;
    }

    public void finalizeCreation(DepositId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do depósito já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((Deposit) other);
    }

    private boolean equalsCasted(Deposit other) {
        return Objects.equals(id, other.id)
                && Objects.equals(userId, other.userId)
                && Objects.equals(amount, other.amount)
                && Objects.equals(createdAt, other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount, createdAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private DepositId id;
        private UserId userId;
        private PositiveMoney amount;
        private TimeMillis createdAt = TimeMillis.now();

        private Builder() {
        }

        public Builder withId(DepositId id) {
            this.id = id;
            return this;
        }

        public Builder withUserId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder withAmount(PositiveMoney amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCreatedAt(TimeMillis createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Deposit build() {
            return new Deposit(this);
        }
    }
}
