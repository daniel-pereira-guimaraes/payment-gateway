package com.danielpg.paymentgateway.domain.usuario;

public class InvalidCpfException extends IllegalArgumentException {

    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + cpf);
    }

}
