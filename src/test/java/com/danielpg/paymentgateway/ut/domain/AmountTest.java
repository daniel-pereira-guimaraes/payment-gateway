package com.danielpg.paymentgateway.ut.domain;

import com.danielpg.paymentgateway.domain.Amount;
import com.danielpg.paymentgateway.domain.TimeMillis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Test
    void addCreatesNewInstanceWithSum() {
        var amount1 = Amount.of(new BigDecimal("10.00"));
        var amount2 = Amount.of(new BigDecimal("2.00"));

        var result = amount1.add(amount2);

        assertThat(amount1.value(), is(new BigDecimal("10.00")));
        assertThat(amount2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("12.00")));
    }

    @Test
    void subtractCreatesNewInstanceWithDifference() {
        var amount1 = Amount.of(new BigDecimal("10.00"));
        var amount2 = Amount.of(new BigDecimal("2.00"));

        var result = amount1.subtract(amount2);

        assertThat(amount1.value(), is(new BigDecimal("10.00")));
        assertThat(amount2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("8.00")));
    }

    @Test
    void compareToReturnsCorrectOrder() {
        var amount1 = Amount.of(BigDecimal.TWO);
        var amount2 = Amount.of(BigDecimal.TEN);
        var amount3 = Amount.of(BigDecimal.TWO);

        assertThat(amount1.compareTo(amount2), lessThan(0));
        assertThat(amount2.compareTo(amount1), greaterThan(0));
        assertThat(amount1.compareTo(amount3), is(0));
    }

}
