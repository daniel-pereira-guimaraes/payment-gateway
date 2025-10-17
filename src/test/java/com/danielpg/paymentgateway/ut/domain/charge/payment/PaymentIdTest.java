package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentIdTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void ofReturnsPaymentIdWhenValid(long value) {
        var paymentId = PaymentId.of(value);

        assertThat(paymentId, notNullValue());
        assertThat(paymentId.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
    void ofThrowsExceptionWhenIdIsNotPositive(long invalidValue) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PaymentId.of(invalidValue)
        );
        assertThat(exception.getMessage(), is("O id deve ser positivo."));
    }

    @Test
    void ofThrowsExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PaymentId.of(null)
        );
        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = PaymentId.ofNullable(42L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(42L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = PaymentId.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        var id1 = PaymentId.of(10L);
        var id2 = PaymentId.of(10L);
        var id3 = PaymentId.of(20L);

        assertThat(id1, is(id2));
        assertThat(id1.hashCode(), is(id2.hashCode()));

        assertThat(id1, not(id3));
        assertThat(id1.hashCode(), not(id3.hashCode()));
    }
}
