package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;

import java.util.Objects;

public class RegisterPaymentRequest {

    private final ChargeId chargeId;
    private final PaymentMethod method;
    private final CreditCard creditCard;

    private RegisterPaymentRequest(Builder builder) {
        this.chargeId = Objects.requireNonNull(builder.chargeId, "O id da cobrança é requerido.");
        this.method = Objects.requireNonNull(builder.method, "O método de pagamento é requerido.");

        if (this.method == PaymentMethod.CREDIT_CARD) {
            this.creditCard = Objects.requireNonNull(builder.creditCard, "O cartão de crédito é requerido para pagamentos com cartão.");
        } else {
            this.creditCard = null;
        }
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChargeId chargeId;
        private PaymentMethod method;
        private CreditCard creditCard;

        private Builder() {}

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

        public RegisterPaymentRequest build() {
            return new RegisterPaymentRequest(this);
        }
    }
}
