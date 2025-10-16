package com.danielpg.paymentgateway.domain.usuario;

public class InvalidEmailAddressException extends IllegalArgumentException {

    public InvalidEmailAddressException(String email, Throwable cause) {
        super("Endereço de e-mail inválido: " + email, cause);
    }

    public InvalidEmailAddressException(String email) {
        this(email, null);
    }

}
