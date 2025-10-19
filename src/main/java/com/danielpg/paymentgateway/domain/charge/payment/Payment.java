package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.shared.Validation;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;

import java.util.Objects;

public class Payment {

    private PaymentId id;
    private final ChargeId chargeId;
    private final PaymentMethod method;
    private final CreditCard creditCard;
    private final TimeMillis paidAt;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.chargeId = Validation.required(builder.chargeId, "O id da cobrança é requerido.");
        this.method = Validation.required(builder.method, "O método de pagamento é requerido.");
        this.paidAt = Validation.required(builder.paidAt, "A data/hora do pagamento é requerida.");

        if (method == PaymentMethod.CREDIT_CARD) {
            this.creditCard = Validation.required(builder.creditCard, "O cartão de crédito é requerido para pagamentos com cartão.");
        } else {
            this.creditCard = null;
        }
    }

    public PaymentId id() {
        return id;
    }

    public ChargeId chargeId() {
        return chargeId;
    }

    public PaymentMethod method() {
        return method;
    }

    public CreditCard creditCard() {
        return creditCard;
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
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((Payment) other);
    }

    private boolean equalsCasted(Payment other) {
        return Objects.equals(id, other.id)
                && Objects.equals(chargeId, other.chargeId)
                && Objects.equals(method, other.method)
                && Objects.equals(creditCard, other.creditCard)
                && Objects.equals(paidAt, other.paidAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chargeId, method, creditCard, paidAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PaymentId id;
        private ChargeId chargeId;
        private PaymentMethod method;
        private CreditCard creditCard;
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

        public Builder withMethod(PaymentMethod method) {
            this.method = method;
            return this;
        }

        public Builder withCreditCard(CreditCard creditCard) {
            this.creditCard = creditCard;
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
