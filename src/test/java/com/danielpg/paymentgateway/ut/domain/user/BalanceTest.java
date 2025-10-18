package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.shared.PositiveMoney;
import com.danielpg.paymentgateway.domain.user.Balance;
import com.danielpg.paymentgateway.domain.user.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BalanceTest {

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "999999999.99"})
    void createsSuccessfully(String stringValue) {
        var value = new BigDecimal(stringValue);

        var balance = Balance.of(value);

        assertThat(balance.value(), is(value));
    }

    @ParameterizedTest
    @CsvSource({"0.015, 0.02", "0.025, 0.02"})
    void mustRoundToHalfEven(String input, String expected) {
        var inputValue = new BigDecimal(input);
        var expectedValue = new BigDecimal(expected);

        var balance = Balance.of(inputValue);

        assertThat(balance.value(), is(expectedValue));
    }

    @Test
    void throwsExceptionWhenValueIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Balance.of(null)
        );

        assertThat(exception.getMessage(), is("O valor é requerido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "1000000000.00"})
    void throwsExceptionWhenValueIsOutOfRange(String input) {
        var value = new BigDecimal(input);

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Balance.of(value)
        );

        assertThat(exception.getMessage(), is("O valor deve ser de 0.00 a 999999999.99."));
    }

    @Test
    void addCreatesNewInstanceWithSum() {
        var balance1 = Balance.of(new BigDecimal("10.00"));
        var balance2 = Balance.of(new BigDecimal("2.00"));

        var result = balance1.add(balance2);

        assertThat(balance1.value(), is(new BigDecimal("10.00")));
        assertThat(balance2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("12.00")));
    }

    @Test
    void subtractCreatesNewInstanceWithDifference() {
        var balance1 = Balance.of(new BigDecimal("10.00"));
        var balance2 = Balance.of(new BigDecimal("2.00"));

        var result = balance1.subtract(balance2);

        assertThat(balance1.value(), is(new BigDecimal("10.00")));
        assertThat(balance2.value(), is(new BigDecimal("2.00")));
        assertThat(result.value(), is(new BigDecimal("8.00")));
    }

    @Test
    void canSubtractSameValue() {
        var balance1 = Balance.of(new BigDecimal("10.00"));
        var balance2 = Balance.of(new BigDecimal("10.00"));

        var result = balance1.subtract(balance2);

        assertThat(balance1.value(), is(new BigDecimal("10.00")));
        assertThat(balance2.value(), is(new BigDecimal("10.00")));
        assertThat(result.value(), is(new BigDecimal("0.00")));
    }


    @Test
    void subtractThrowsExceptionWhenValueIsLessThanAmountToSubtract() {
        var balance = Balance.of(BigDecimal.TEN);
        var subtractValue = PositiveMoney.of(balance.value().add(new BigDecimal("0.01")));

        var exception = assertThrows(InsufficientBalanceException.class,
                () -> balance.subtract(subtractValue)
        );

        assertThat(exception.getMessage(), is("Saldo insuficiente."));
    }

    @Test
    void compareToReturnsCorrectOrder() {
        var balance1 = Balance.of(BigDecimal.TWO);
        var balance2 = Balance.of(BigDecimal.TEN);
        var balance3 = Balance.of(BigDecimal.TWO);

        assertThat(balance1.compareTo(balance2), lessThan(0));
        assertThat(balance2.compareTo(balance1), greaterThan(0));
        assertThat(balance1.compareTo(balance3), is(0));
    }

}
