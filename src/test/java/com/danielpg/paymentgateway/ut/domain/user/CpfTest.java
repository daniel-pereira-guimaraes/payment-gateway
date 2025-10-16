package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.Cpf;
import com.danielpg.paymentgateway.domain.user.InvalidCpfException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CpfTest {

    @ParameterizedTest
    @ValueSource(strings = { "00000000191", "99999999808" })
    void createSuccessfully(String value) {
        var cpf = Cpf.of(value);

        assertThat(cpf.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000107", "99999999812", "11111111111"
    })
    void throwsExceptionWhenCpfIsInvalid(String value) {
        var exception = assertThrows(InvalidCpfException.class,
                () -> Cpf.of(value)
        );

        assertThat(exception.getMessage(), is("CPF inválido: " + value));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void returnsEmptyWhenValueIsBlank(String value) {
        var cpfCnpj = Cpf.ofNullable(value);

        assertThat(cpfCnpj.isEmpty(), is(true));
    }
}
