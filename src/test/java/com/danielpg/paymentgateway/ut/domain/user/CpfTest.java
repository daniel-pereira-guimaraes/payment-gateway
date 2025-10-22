package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.Cpf;
import com.danielpg.paymentgateway.domain.user.InvalidCpfException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.danielpg.paymentgateway.fixture.CpfFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CpfTest {

    @ParameterizedTest
    @ValueSource(strings = { "00000000191", " 99999999808 " })
    void createSuccessfully(String value) {
        var cpf = Cpf.of(value);

        assertThat(cpf.value(), is(value.trim()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000107", "99999999812", "11111111111"
    })
    void throwsExceptionWhenCpfIsInvalid(String value) {
        var exception = assertThrows(InvalidCpfException.class,
                () -> Cpf.of(value)
        );

        assertThat(exception.getMessage(), containsString("CPF inválido"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void returnsEmptyWhenValueIsBlank(String value) {
        var cpfCnpj = Cpf.ofNullable(value);

        assertThat(cpfCnpj.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeEqualWhenValueIsSame() {
        var cpf1 = Cpf.of(CPF1_VALUE);
        var cpf2 = Cpf.of(CPF1_VALUE);
        var cpf3 = Cpf.of(CPF2_VALUE);

        assertThat(cpf1, is(cpf2));
        assertThat(cpf1, not(cpf3));
        assertThat(cpf1, not(CPF1_VALUE));
        assertThat(cpf1.hashCode(), is(cpf2.hashCode()));
        assertThat(cpf1.hashCode(), not(cpf3.hashCode()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void ofNullableReturnsEmptyWhenValueIsBlank(String value) {
        var result = Cpf.ofNullable(value);
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = Cpf.ofNullable(CPF1_VALUE);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(CPF1_VALUE));
    }
}
