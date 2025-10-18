package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.TimeMillis;
import com.danielpg.paymentgateway.domain.Validation;
import com.danielpg.paymentgateway.domain.charge.ChargeId;

import java.util.Objects;

public class Payment {

    private PaymentId id;
    private final ChargeId chargeId;
    private final TimeMillis paidAt;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.chargeId = Validation.required(builder.chargeId, "O id da cobrança é requerido.");
        this.paidAt = Validation.required(builder.paidAt, "A data/hora do pagamento é requerida.");
    }

    public PaymentId id() {
        return id;
    }

    public ChargeId chargeId() {
        return chargeId;
    }

    public TimeMillis paidAt() {
        return paidAt;
    }

    public void finalizeCreation(PaymentId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do pagamento já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) && Objects.equals(chargeId, payment.chargeId) && Objects.equals(paidAt, payment.paidAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeId, paidAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PaymentId id;
        private ChargeId chargeId;
        private TimeMillis paidAt;

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

        public Builder withPaidAt(TimeMillis paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
