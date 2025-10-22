package com.danielpg.paymentgateway.domain.user;

import com.danielpg.paymentgateway.domain.shared.DataMasking;

public class InvalidEmailAddressException extends IllegalArgumentException {

    public InvalidEmailAddressException(String email, Throwable cause) {
        super("Endereço de e-mail inválido: " + DataMasking.maskEmail(email), cause);
    }

    public InvalidEmailAddressException(String email) {
        this(email, null);
    }

}
