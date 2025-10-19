package com.danielpg.paymentgateway.ut.domain.shared;

import com.danielpg.paymentgateway.domain.shared.CreditCardExpirationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.YearMonth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardExpirationValidatorTest {

    private YearMonth now;
    private int currentYearFull;
    private int currentYearTwoDigits;
    private int currentMonth;

    @BeforeEach
    void setUp() {
        now = YearMonth.now();
        currentYearFull = now.getYear();
        currentYearTwoDigits = currentYearFull % 100;
        currentMonth = now.getMonthValue();
    }

    @Test
    void returnsValueWhenDateIsCurrentMonthOrFuture() {
        var currentMonthStr = String.format("%02d/%02d", currentMonth, currentYearTwoDigits);
        var currentMonthFullStr = String.format("%02d/%04d", currentMonth, currentYearFull);
        var nextMonth = now.plusMonths(1);
        var nextMonthStr = String.format("%02d/%02d", nextMonth.getMonthValue(), nextMonth.getYear() % 100);

        assertThat(CreditCardExpirationValidator.validate(currentMonthStr), is(currentMonthStr));
        assertThat(CreditCardExpirationValidator.validate(currentMonthFullStr), is(currentMonthFullStr));
        assertThat(CreditCardExpirationValidator.validate(nextMonthStr), is(nextMonthStr));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    void throwsExceptionWhenDateIsBlank(String input) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationValidator.validate(input));

        assertThat(exception.getMessage(), is("Data de expiração é requerida."));
    }

    @Test
    void throwsExceptionWhenDateIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationValidator.validate(null));

        assertThat(exception.getMessage(), is("Data de expiração é requerida."));
    }

    @Test
    void throwsExceptionWhenMonthIsBeforeCurrentMonth() {
        var previousMonth = now.minusMonths(1);
        var previousMonthStr = String.format("%02d/%02d", previousMonth.getMonthValue(), previousMonth.getYear() % 100);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationValidator.validate(previousMonthStr));

        assertThat(exception.getMessage(), is("Data de expiração inválida."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0/25", "13/25", "01/abcd", "12-2025", "122025", "12/", "/2025", "ab/cd"
    })
    void throwsExceptionWhenFormatIsInvalid(String input) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationValidator.validate(input));

        assertThat(exception.getMessage(), is("Data de expiração inválida."));
    }
}
