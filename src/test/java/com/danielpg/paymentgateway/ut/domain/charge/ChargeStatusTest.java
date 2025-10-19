package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChargeStatusTest {

    @Test
    void fromCsvReturnsEmptySetWhenNull() {
        var result = ChargeStatus.fromCsv(null);
        assertThat(result, is(empty()));
    }

    @Test
    void fromCsvReturnsEmptySetWhenBlank() {
        var result = ChargeStatus.fromCsv("   ");
        assertThat(result, is(empty()));
    }

    @Test
    void fromCsvReturnsSingleStatus() {
        var result = ChargeStatus.fromCsv("PENDING");
        assertThat(result, containsInAnyOrder(ChargeStatus.PENDING));
    }

    @Test
    void fromCsvReturnsMultipleStatuses() {
        var result = ChargeStatus.fromCsv("PENDING,PAID,CANCELED");
        assertThat(result, containsInAnyOrder(
                ChargeStatus.PENDING,
                ChargeStatus.PAID,
                ChargeStatus.CANCELED
        ));
    }

    @Test
    void fromCsvTrimsSpacesAndIsCaseInsensitive() {
        var result = ChargeStatus.fromCsv(" pending , Paid ");
        assertThat(result, containsInAnyOrder(
                ChargeStatus.PENDING,
                ChargeStatus.PAID
        ));
    }

    @Test
    void fromCsvThrowsExceptionForInvalidStatus() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> ChargeStatus.fromCsv("PENDING,INVALID"));
        assertThat(exception.getMessage(), is("Status inválido: INVALID"));
    }
}
