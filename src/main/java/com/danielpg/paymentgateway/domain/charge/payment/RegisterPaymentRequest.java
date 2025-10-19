package com.danielpg.paymentgateway.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;

import java.util.Objects;

public class RegisterPaymentRequest {

    private final Charge charge;
    private final PaymentMethod method;
    private final CreditCard creditCard;

    private RegisterPaymentRequest(Builder builder) {
        this.charge = Objects.requireNonNull(builder.charge, "A cobrança é requerida.");
        this.method = Objects.requireNonNull(builder.method, "O método de pagamento é requerido.");

        if (this.method == PaymentMethod.CREDIT_CARD) {
            this.creditCard = Objects.requireNonNull(builder.creditCard, "O cartão de crédito é requerido para pagamentos com cartão.");
        } else {
            this.creditCard = null;
        }
    }

    public Charge charge() {
        return charge;
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
        private Charge charge;
        private PaymentMethod method;
        private CreditCard creditCard;

        private Builder() {}

        public Builder withCharge(Charge charge) {
            this.charge = charge;
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
