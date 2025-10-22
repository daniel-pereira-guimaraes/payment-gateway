package com.danielpg.paymentgateway.domain.deposit;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.shared.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;

import java.util.Objects;

public class DepositRequest {

    private final UserId userId;
    private final PositiveMoney amount;

    private DepositRequest(UserId userId, PositiveMoney amount) {
        this.userId = Validation.required(userId, "O usuário é requerido.");
        this.amount = Validation.required(amount, "O valor é requerido.");
    }

    public static DepositRequest of(UserId userId, PositiveMoney amount) {
        return new DepositRequest(userId, amount);
    }

    public UserId userId() {
        return userId;
    }

    public PositiveMoney amount() {
        return amount;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        DepositRequest that = (DepositRequest) other;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, amount);
    }
}
