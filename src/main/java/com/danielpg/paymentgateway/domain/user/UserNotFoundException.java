package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.AbstractNotFoundException;

public class UserNotFoundException extends AbstractNotFoundException {

    public static final String USER_WITH_CPF_NOT_FOUND = "Usuário com CPF %s não encontrado.";
    public static final String USER_WITH_ID_NOT_FOUND = "Usuário com ID %d não encontrado.";

    public UserNotFoundException(Cpf cpf) {
        super(USER_WITH_CPF_NOT_FOUND.formatted(cpf.value()));
    }

    public UserNotFoundException(UserId id) {
        super(USER_WITH_ID_NOT_FOUND.formatted(id.value()));
    }
}
