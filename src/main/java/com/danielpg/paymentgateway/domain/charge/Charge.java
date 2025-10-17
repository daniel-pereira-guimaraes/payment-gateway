package com.danielpg.paymentgateway.domain.charge;

import com.danielpg.paymentgateway.domain.Validation;
import com.danielpg.paymentgateway.domain.user.UserId;
import io.micrometer.common.util.StringUtils;

public class Charge {

    private ChargeId id;
    private final UserId issuerId;
    private final UserId payerId;
    private final Amount amount;
    private final String description;

    private Charge(Builder builder) {
        this.id = builder.id;
        this.issuerId = Validation.required(builder.issuerId, "O emitente é requerido.");
        this.payerId = Validation.required(builder.payerId, "O pagador é requerido.");
        this.amount = Validation.required(builder.amount, "O valor é requerido.");
        this.description = StringUtils.isBlank(builder.description) ? null : builder.description.trim();
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

    public Amount amount() {
        return amount;
    }

    public String description() {
        return description;
    }

    public void finalizeCreation(ChargeId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação da cobrança já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ChargeId id;
        private UserId issuerId;
        private UserId payerId;
        private Amount amount;
        private String description;

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

        public Builder withAmount(Amount amount) {
            this.amount = amount;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Charge build() {
            return new Charge(this);
        }
    }
}
