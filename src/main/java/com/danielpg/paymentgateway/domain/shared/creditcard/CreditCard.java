package com.danielpg.paymentgateway.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.Validation;

import java.util.Objects;

public class CreditCard {

    private final CreditCardNumber number;
    private final CreditCardExpirationDate expirationDate;
    private final CreditCardCvv cvv;

    private CreditCard(Builder builder) {
        this.number = Validation.required(builder.number, "O número do cartão é requerido.");
        this.expirationDate = Validation.required(builder.expirationDate, "A data de expiração do cartão é requerida.");
        this.cvv = Validation.required(builder.cvv, "O cvv do cartão é requerido.");
    }

    public CreditCardNumber number() {
        return number;
    }

    public CreditCardExpirationDate expirationDate() {
        return expirationDate;
    }

    public CreditCardCvv cvv() {
        return cvv;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((CreditCard) other);
    }

    private boolean equalsCasted(CreditCard other) {
        return Objects.equals(number, other.number)
                && Objects.equals(expirationDate,
                other.expirationDate)
                && Objects.equals(cvv, other.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, expirationDate, cvv);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private CreditCardNumber number;
        private CreditCardExpirationDate expirationDate;
        private CreditCardCvv cvv;

        private Builder() {
        }

        public Builder withNumber(CreditCardNumber number) {
            this.number = number;
            return this;
        }

        public Builder withExpirationDate(CreditCardExpirationDate expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder withCvv(CreditCardCvv cvv) {
            this.cvv = cvv;
            return this;
        }

        public CreditCard build() {
            return new CreditCard(this);
        }
    }
}
