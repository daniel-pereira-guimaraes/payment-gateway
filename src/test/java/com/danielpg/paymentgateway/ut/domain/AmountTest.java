package com.danielpg.paymentgateway.ut.domain;

import com.danielpg.paymentgateway.domain.Amount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmountTest {

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "999999999.99"})
    void createSuccessfully(String stringValue) {
        var value = new BigDecimal(stringValue);

        var amount = Amount.of(value);

        assertThat(amount.value(), is(value));
    }

    @ParameterizedTest
    @CsvSource({"0.015, 0.02", "0.025, 0.02"})
    void mustAroundToHalfEven(String input, String expected) {
        var inputValue = new BigDecimal(input);
        var expectedValue = new BigDecimal(expected);

        var amount = Amount.of(inputValue);

        assertThat(amount.value(), is(expectedValue));
    }

    @Test
    void throwsExceptionWhenValueIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Amount.of(null)
        );

        assertThat(exception.getMessage(), is("O valor é requerido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "0.00", "1000000000.00"})
    void throwsExceptionWhenValueOutOfRange(String input) {
        var value = new BigDecimal(input);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Amount.of(value)
        );

        assertThat(exception.getMessage(), is("O valor deve ser de 0.01 a 999999999.99."));
    }

}
