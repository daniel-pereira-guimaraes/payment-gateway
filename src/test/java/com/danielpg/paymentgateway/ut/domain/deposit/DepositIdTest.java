package com.danielpg.paymentgateway.ut.domain.deposit;

import com.danielpg.paymentgateway.domain.deposit.DepositId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepositIdTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void ofReturnsDepositIdWhenValid(long value) {
        var depositId = DepositId.of(value);

        assertThat(depositId, notNullValue());
        assertThat(depositId.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
    void ofThrowsExceptionWhenIdIsNotPositive(long invalidValue) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                DepositId.of(invalidValue)
        );
        assertThat(exception.getMessage(), is("O id deve ser positivo."));
    }

    @Test
    void ofThrowsExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                DepositId.of(null)
        );
        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = DepositId.ofNullable(42L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(42L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = DepositId.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        var id1 = DepositId.of(10L);
        var id2 = DepositId.of(10L);
        var id3 = DepositId.of(20L);

        assertThat(id1, is(id2));
        assertThat(id1.hashCode(), is(id2.hashCode()));

        assertThat(id1, not(id3));
        assertThat(id1.hashCode(), not(id3.hashCode()));
    }
}
