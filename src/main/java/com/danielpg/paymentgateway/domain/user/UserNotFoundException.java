package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.AbstractNotFoundException;

public class UserNotFoundException extends AbstractNotFoundException {

    public UserNotFoundException(Cpf cpf) {
        super("Usuário com CPF %s não encontrado.".formatted(cpf.value()));
    }
}
