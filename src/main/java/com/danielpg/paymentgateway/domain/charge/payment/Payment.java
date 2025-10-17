package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.Validation;
import com.danielpg.paymentgateway.domain.charge.ChargeId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Payment {

    private PaymentId id;
    private final ChargeId chargeId;
    private final Instant paidAt;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.chargeId = Validation.required(builder.chargeId, "O id da cobrança é requerido.");
        this.paidAt = validatePaidAt(builder.paidAt);
    }

    private static Instant validatePaidAt(Instant paidAt) {
        Validation.required(paidAt, "A data/hora do pagamento é requerida.");
        return paidAt.truncatedTo(ChronoUnit.MILLIS);
    }

    public PaymentId id() {
        return id;
    }

    public ChargeId chargeId() {
        return chargeId;
    }

    public Instant paidAt() {
        return paidAt;
    }

    public void finalizeCreation(PaymentId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do pagamento já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PaymentId id;
        private ChargeId chargeId;
        private Instant paidAt;

        private Builder() {
        }

        public Builder withId(PaymentId id) {
            this.id = id;
            return this;
        }

        public Builder withChargeId(ChargeId chargeId) {
            this.chargeId = chargeId;
            return this;
        }

        public Builder withPaidAt(Instant paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
