package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.PositiveMoney;
import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.domain.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;
import io.micrometer.common.util.StringUtils;

public class Charge {

    private ChargeId id;
    private final UserId issuerId;
    private final UserId payerId;
    private final PositiveMoney amount;
    private final String description;
    private final TimeMillis createdAt;
    private final TimeMillis dueAt;
    private ChargeStatus status;

    private Charge(Builder builder) {
        this.id = builder.id;
        this.issuerId = Validation.required(builder.issuerId, "O emitente é requerido.");
        this.payerId = Validation.required(builder.payerId, "O pagador é requerido.");
        this.amount = Validation.required(builder.amount, "O valor é requerido.");
        this.description = StringUtils.isBlank(builder.description) ? null : builder.description.trim();
        this.createdAt = Validation.required(builder.createdAt, "A data/hora de criação é requerida.");
        this.dueAt = validateDueAt(builder);
        this.status = Validation.required(builder.status, "O status da cobrança é requerido.");
    }

    private static TimeMillis validateDueAt(Builder builder) {
        Validation.required(builder.dueAt, "A data/hora de vencimento é requerida.");
        if (builder.dueAt.compareTo(builder.createdAt) <= 0) {
            throw new IllegalArgumentException("A data/hora de vencimento deve ser posterior à data de criação.");
        }
        return builder.dueAt;
    }

    public ChargeId id() {
        return id;
    }

    public UserId issuerId() {
        return issuerId;
    }

    public UserId payerId() {
        return payerId;
    }

    public PositiveMoney amount() {
        return amount;
    }

    public String description() {
        return description;
    }

    public TimeMillis createdAt() {
        return createdAt;
    }

    public TimeMillis dueAt() {
        return dueAt;
    }

    public ChargeStatus status() {
        return status;
    }

    public void finalizeCreation(ChargeId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação da cobrança já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    public void changeStatusToPaid() {
        ensurePendingStatus();
        this.status = ChargeStatus.PAID;
    }

    public void changeStatusToCanceled() {
        ensurePendingStatus();
        this.status = ChargeStatus.CANCELED;
    }

    private void ensurePendingStatus() {
        if (status != ChargeStatus.PENDING) {
            throw new IllegalStateException("A cobrança não está pendente.");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ChargeId id;
        private UserId issuerId;
        private UserId payerId;
        private PositiveMoney amount;
        private String description;
        private TimeMillis createdAt;
        private TimeMillis dueAt;
        private ChargeStatus status;

        private Builder() {
        }

        public Builder withId(ChargeId id) {
            this.id = id;
            return this;
        }

        public Builder withIssuerId(UserId issuerId) {
            this.issuerId = issuerId;
            return this;
        }

        public Builder withPayerId(UserId payerId) {
            this.payerId = payerId;
            return this;
        }

        public Builder withAmount(PositiveMoney amount) {
            this.amount = amount;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCreatedAt(TimeMillis createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withDueAt(TimeMillis dueAt) {
            this.dueAt = dueAt;
            return this;
        }

        public Builder withStatus(ChargeStatus status) {
            this.status = status;
            return this;
        }

        public Charge build() {
            return new Charge(this);
        }
    }
}
