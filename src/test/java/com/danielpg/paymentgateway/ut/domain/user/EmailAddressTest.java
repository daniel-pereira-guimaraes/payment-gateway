package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.EmailAddress;
import com.danielpg.paymentgateway.domain.user.InvalidEmailAddressException;
import org.junit.jupiter.api.Test;
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
            " name.etc@server.com "
    })
    void createSuccessfully(String address) {
        var email = EmailAddress.of(address);

        assertThat(email.value(), is(address.trim()));
    }

    @Test
    void throwsExceptionWhenAddressIsNull() {
        assertThrows(NullPointerException.class, () -> EmailAddress.of(null));
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

    @Test
    void returnsEmptyOptionalWhenAddressIsNull() {
        var result = EmailAddress.ofNullable(null);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void returnsEmailAddressWhenAddressIsNotNull() {
        var result = EmailAddress.ofNullable("user@example.com");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is("user@example.com"));
    }

}
