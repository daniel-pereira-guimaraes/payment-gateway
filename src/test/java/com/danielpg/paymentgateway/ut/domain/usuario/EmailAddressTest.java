package com.danielpg.paymentgateway.ut.domain.usuario;

import com.danielpg.paymentgateway.domain.usuario.EmailAddress;
import com.danielpg.paymentgateway.domain.usuario.InvalidEmailAddressException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "name@server",
            "name@server.com",
            "name@server.com.br",
            "name.etc@server.com"
    })
    void createSuccessfully(String address) {
        var email = EmailAddress.of(address);

        assertThat(email.value(), is(address));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "name", "name@",
            "server.com", "@server.com",
            "Name <user@example.com>"
    })
    void throwsExceptionWhenCreatingWithInvalidEmailAddress(String address) {
        assertThrows(InvalidEmailAddressException.class, () -> EmailAddress.of(address));
    }

}
