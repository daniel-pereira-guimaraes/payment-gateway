package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.AbstractNotFoundException;

public class UserNotFoundException extends AbstractNotFoundException {

    public static final String USER_WITH_CPF_NOT_FOUND = "Usuário com CPF %s não encontrado.";

    public UserNotFoundException(Cpf cpf) {
        super(USER_WITH_CPF_NOT_FOUND.formatted(cpf.value()));
    }
}
