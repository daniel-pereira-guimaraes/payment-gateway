package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.AbstractNotFoundException;
import com.danielpg.paymentgateway.domain.shared.DataMasking;

public class UserNotFoundException extends AbstractNotFoundException {

    public static final String USER_WITH_CPF_NOT_FOUND = "Usuário com CPF %s não encontrado.";
    public static final String USER_WITH_ID_NOT_FOUND = "Usuário com ID %d não encontrado.";
    public static final String USER_WITH_EMAIL_NOT_FOUND = "Usuário com email %s não encontrado.";

    public UserNotFoundException(Cpf cpf) {
        super(USER_WITH_CPF_NOT_FOUND.formatted(
                DataMasking.maskCpf(cpf.value())
        ));
    }

    public UserNotFoundException(UserId id) {
        super(USER_WITH_ID_NOT_FOUND.formatted(id.value()));
    }

    public UserNotFoundException(EmailAddress emailAddress) {
        super(USER_WITH_EMAIL_NOT_FOUND.formatted(
                DataMasking.maskEmail(emailAddress.value())
        ));
    }
}
