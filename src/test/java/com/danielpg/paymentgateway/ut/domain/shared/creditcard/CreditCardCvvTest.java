package com.danielpg.paymentgateway.ut.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardCvv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardCvvTest {

    @ParameterizedTest
    @ValueSource(strings = { "123", "999", "000", "1234" })
    void returnsValueWhenCvvIsValid(String validCvv) {
        var cvv = CreditCardCvv.of(validCvv);

        assertThat(cvv.value(), is(validCvv));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "12", "12345", "abcd", "12a", " 12 ", "123 4" })
    void throwsExceptionWhenCvvIsInvalid(String invalidCvv) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardCvv.of(invalidCvv));

        assertThat(exception.getMessage(), is("CVV inválido."));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    void returnsEmptyOptionalWhenCvvIsBlankUsingOfNullable(String blank) {
        Optional<CreditCardCvv> opt = CreditCardCvv.ofNullable(blank);

        assertThat(opt.isEmpty(), is(true));
    }

    @Test
    void returnsOptionalWhenCvvIsValidUsingOfNullable() {
        var valid = "123";

        Optional<CreditCardCvv> opt = CreditCardCvv.ofNullable(valid);

        assertThat(opt.isPresent(), is(true));
        assertThat(opt.get().value(), is(valid));
    }

    @Test
    void trimsCvvBeforeValidation() {
        var cvvWithSpaces = " 123 ";

        var cvv = CreditCardCvv.of(cvvWithSpaces);

        assertThat(cvv.value(), is("123"));
    }

    @Test
    void equalsAndHashCodeWorkCorrectly() {
        var cvv1 = CreditCardCvv.of("123");
        var cvv2 = CreditCardCvv.of("123");
        var cvv3 = CreditCardCvv.of("999");

        assertThat(cvv1.equals(cvv2), is(true));
        assertThat(cvv1.equals(cvv3), is(false));
        assertThat(cvv1.hashCode(), is(cvv2.hashCode()));
    }
}
