package com.danielpg.paymentgateway.ut.domain;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositiveMoneyTest {

    @ParameterizedTest
    @ValueSource(strings = {"0.01", "999999999.99"})
    void createSuccessfully(String stringValue) {
        var value = new BigDecimal(stringValue);

        var positiveMoney = PositiveMoney.of(value);

        assertThat(positiveMoney.value(), is(value));
    }

    @ParameterizedTest
    @CsvSource({"0.015, 0.02", "0.025, 0.02"})
    void mustAroundToHalfEven(String input, String expected) {
        var inputValue = new BigDecimal(input);
        var expectedValue = new BigDecimal(expected);

        var positiveMoney = PositiveMoney.of(inputValue);

        assertThat(positiveMoney.value(), is(expectedValue));
    }

    @Test
    void throwsExceptionWhenValueIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> PositiveMoney.of(null)
        );

        assertThat(exception.getMessage(), is("O valor é requerido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "0.00", "1000000000.00"})
    void throwsExceptionWhenValueOutOfRange(String input) {
        var value = new BigDecimal(input);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> PositiveMoney.of(value)
        );

        assertThat(exception.getMessage(), is("O valor deve ser de 0.01 a 999999999.99."));
    }

    @Test
    void addCreatesNewInstanceWithSum() {
        var positiveMoney1 = PositiveMoney.of(new BigDecimal("10.00"));
        var positiveMoney2 = PositiveMoney.of(new BigDecimal("2.00"));

        var result = positiveMoney1.add(positiveMoney2);

        assertThat(positiveMoney1.value(), is(new BigDecimal("10.00")));
        assertThat(positiveMoney2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("12.00")));
    }

    @Test
    void subtractCreatesNewInstanceWithDifference() {
        var positiveMoney1 = PositiveMoney.of(new BigDecimal("10.00"));
        var positiveMoney2 = PositiveMoney.of(new BigDecimal("2.00"));

        var result = positiveMoney1.subtract(positiveMoney2);

        assertThat(positiveMoney1.value(), is(new BigDecimal("10.00")));
        assertThat(positiveMoney2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("8.00")));
    }

    @Test
    void compareToReturnsCorrectOrder() {
        var positiveMoney1 = PositiveMoney.of(BigDecimal.TWO);
        var positiveMoney2 = PositiveMoney.of(BigDecimal.TEN);
        var positiveMoney3 = PositiveMoney.of(BigDecimal.TWO);

        assertThat(positiveMoney1.compareTo(positiveMoney2), lessThan(0));
        assertThat(positiveMoney2.compareTo(positiveMoney1), greaterThan(0));
        assertThat(positiveMoney1.compareTo(positiveMoney3), is(0));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var value = new BigDecimal("123.45");

        var result = PositiveMoney.ofNullable(value);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(value));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = PositiveMoney.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }
}
