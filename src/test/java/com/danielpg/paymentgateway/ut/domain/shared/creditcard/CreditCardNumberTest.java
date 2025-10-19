package com.danielpg.paymentgateway.ut.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardNumber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardNumberTest {

    @ParameterizedTest
    @ValueSource(strings = { "1234567890123", "4111111111111111", "1234567890123456789" })
    void returnsValueWhenNumberIsValid(String validNumber) {
        var card = CreditCardNumber.of(validNumber);

        assertThat(card.value(), is(validNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123456789012", "12345678901234567890", "4111 1111 1111 1111", "abcd1234567890" })
    void throwsExceptionWhenNumberIsInvalid(String invalidNumber) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardNumber.of(invalidNumber));

        assertThat(exception.getMessage(), is("Número de cartão de crédito inválido."));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    ", })
    void returnsEmptyOptionalWhenNumberIsBlankUsingOfNullable(String blankValue) {
        Optional<CreditCardNumber> opt = CreditCardNumber.ofNullable(blankValue);

        assertThat(opt.isEmpty(), is(true));
    }

    @Test
    void returnsOptionalWhenNumberIsValidUsingOfNullable() {
        var validNumber = "4111111111111111";

        Optional<CreditCardNumber> opt = CreditCardNumber.ofNullable(validNumber);

        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().value(), is(validNumber));
    }

    @Test
    void trimsNumberBeforeValidation() {
        var numberWithSpaces = " 4111111111111111 ";

        var card = CreditCardNumber.of(numberWithSpaces);

        assertThat(card.value(), is("4111111111111111"));
    }

    @Test
    void equalsAndHashCodeWorkCorrectly() {
        var number1 = CreditCardNumber.of("4111111111111111");
        var number2 = CreditCardNumber.of("4111111111111111");
        var number3 = CreditCardNumber.of("1234567890123");

        assertThat(number1.equals(number2), is(true));
        assertThat(number1.equals(number3), is(false));
        assertThat(number1.hashCode(), is(number2.hashCode()));
    }
}
