package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.PositiveMoney;
import com.danielpg.paymentgateway.domain.Validation;

import java.util.Objects;

public class User {

    private UserId id;
    private final PersonName name;
    private final Cpf cpf;
    private final EmailAddress emailAddress;
    private final HashedPassword hashedPassword;
    private Balance balance;

    private User(Builder builder) {
        this.id = builder.id;
        this.name = Validation.required(builder.name, "O nome é requerido.");
        this.cpf = Validation.required(builder.cpf, "O CPF é requerido.");
        this.emailAddress = Validation.required(builder.emailAddress1, "O e-mail é requerido.");
        this.hashedPassword = Validation.required(builder.hashedPassword, "A senha é requerida.");
        this.balance = Validation.required(builder.balance, "O saldo é requerido.");
    }

    public UserId id() {
        return id;
    }

    public PersonName name() {
        return name;
    }

    public Cpf cpf() {
        return cpf;
    }

    public EmailAddress emailAddress() {
        return emailAddress;
    }

    public HashedPassword hashedPassword() {
        return hashedPassword;
    }

    public Balance balance() {
        return balance;
    }

    public void increaseBalance(PositiveMoney amount) {
        this.balance = this.balance.add(amount);
    }

    public void decreaseBalance(PositiveMoney amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void finalizeCreation(UserId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do usuário já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cpf, emailAddress, hashedPassword, balance);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((User) other);
    }

    private boolean equalsCasted(User other) {
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(cpf, other.cpf)
                && Objects.equals(emailAddress, other.emailAddress)
                && Objects.equals(hashedPassword, other.hashedPassword)
                && Objects.equals(balance, other.balance);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserId id;
        private PersonName name;
        private Cpf cpf;
        private EmailAddress emailAddress1;
        private HashedPassword hashedPassword;
        private Balance balance;

        private Builder() {
        }

        public Builder withId(UserId id) {
            this.id = id;
            return this;
        }

        public Builder withName(PersonName name) {
            this.name = name;
            return this;
        }

        public Builder withCpf(Cpf cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder withEmailAddress(EmailAddress emailAddress) {
            this.emailAddress1 = emailAddress;
            return this;
        }

        public Builder withHashedPassword(HashedPassword hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public Builder withBalance(Balance balance) {
            this.balance = balance;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
