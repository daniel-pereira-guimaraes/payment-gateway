package com.danielpg.paymentgateway.ut.domain.shared.creditcard;

import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCardExpirationDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.YearMonth;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardExpirationDateTest {

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

        assertThat(CreditCardExpirationDate.of(currentMonthStr).value(), is(currentMonthStr));
        assertThat(CreditCardExpirationDate.of(currentMonthFullStr).value(), is(currentMonthFullStr));
        assertThat(CreditCardExpirationDate.of(nextMonthStr).value(), is(nextMonthStr));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "    " })
    void throwsExceptionWhenDateIsBlank(String input) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationDate.of(input));

        assertThat(exception.getMessage(), is("Data de expiração é requerida."));
    }

    @Test
    void throwsExceptionWhenDateIsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationDate.of(null));

        assertThat(exception.getMessage(), is("Data de expiração é requerida."));
    }

    @Test
    void returnsEmptyOptionalWhenDateIsNullOrBlankUsingOfNullable() {
        assertThat(CreditCardExpirationDate.ofNullable(null), is(Optional.empty()));
        assertThat(CreditCardExpirationDate.ofNullable(" "), is(Optional.empty()));
    }

    @Test
    void throwsExceptionWhenMonthIsBeforeCurrentMonth() {
        var previousMonth = now.minusMonths(1);
        var previousMonthStr = String.format("%02d/%02d", previousMonth.getMonthValue(), previousMonth.getYear() % 100);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationDate.of(previousMonthStr));

        assertThat(exception.getMessage(), is("Data de expiração inválida."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0/25", "13/25", "01/abcd", "12-2025", "122025", "12/", "/2025", "ab/cd"
    })
    void throwsExceptionWhenFormatIsInvalid(String input) {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardExpirationDate.of(input));

        assertThat(exception.getMessage(), is("Data de expiração inválida."));
    }

    @Test
    void testEqualsAndHashCodeForSameAndDifferentValues() {
        var date1 = CreditCardExpirationDate.of("12/25");
        var date2 = CreditCardExpirationDate.of("12/25");
        var date3 = CreditCardExpirationDate.of("01/26");

        assertThat(date1, is(date2));
        assertThat(date1.hashCode(), is(date2.hashCode()));
        assertThat(date1, not(date3));
    }
}
