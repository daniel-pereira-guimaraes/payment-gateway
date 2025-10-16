package com.danielpg.paymentgateway.domain.user;

public class InvalidCpfException extends IllegalArgumentException {

    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + cpf);
    }

}
