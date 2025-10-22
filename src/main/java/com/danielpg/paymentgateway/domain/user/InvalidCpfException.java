package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.DataMasking;

public class InvalidCpfException extends IllegalArgumentException {

    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + DataMasking.maskCpf(cpf));
    }

}
