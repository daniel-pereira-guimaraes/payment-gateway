package com.danielpg.paymentgateway.ut.domain;

import com.danielpg.paymentgateway.domain.TimeMillis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeMillisTest {

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE})
    void ofReturnsTimeMillisWhenValid(long value) {
        var time = TimeMillis.of(value);

        assertThat(time, notNullValue());
        assertThat(time.value(), is(value));
    }

    @Test
    void ofThrowsExceptionWhenValueIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                TimeMillis.of(null)
        );
        assertThat(exception.getMessage(), is("O timestamp em milissegundos é requerido."));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = TimeMillis.ofNullable(10L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(10L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = TimeMillis.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeReturnsCorrectly() {
        var t1 = TimeMillis.of(10L);
        var t2 = TimeMillis.of(10L);
        var t3 = TimeMillis.of(11L);

        assertThat(t1, is(t2));
        assertThat(t1.hashCode(), is(t2.hashCode()));

        assertThat(t1, not(t3));
        assertThat(t1.hashCode(), not(t3.hashCode()));
    }

    @Test
    void isBeforeReturnsCorrectlyValue() {
        var t1 = TimeMillis.of(100L);
        var t2 = TimeMillis.of(100L);
        var t3 = TimeMillis.of(101L);

        assertThat(t1.isBefore(t1), is(false));
        assertThat(t1.isBefore(t2), is(false));
        assertThat(t2.isBefore(t3), is(true));
        assertThat(t3.isBefore(t2), is(false));
    }

    @Test
    void isAfterReturnsCorrectlyValue() {
        var t1 = TimeMillis.of(100L);
        var t2 = TimeMillis.of(100L);
        var t3 = TimeMillis.of(101L);

        assertThat(t1.isAfter(t1), is(false));
        assertThat(t1.isAfter(t2), is(false));
        assertThat(t2.isAfter(t3), is(false));
        assertThat(t3.isAfter(t2), is(true));
    }

    @Test
    void compareToReturnsCorrectOrder() {
        var t1 = TimeMillis.of(50L);
        var t2 = TimeMillis.of(100L);
        var t3 = TimeMillis.of(50L);

        assertThat(t1.compareTo(t2), lessThan(0));
        assertThat(t2.compareTo(t1), greaterThan(0));
        assertThat(t1.compareTo(t3), is(0));
    }

    @Test
    void nowReturnsNonNullValue() {
        var time = TimeMillis.now();

        assertThat(time, notNullValue());
        assertThat(time.value(), greaterThan(0L));
    }

    @Test
    void nowReturnsIncreasingValues() throws InterruptedException {
        var t1 = TimeMillis.now();
        Thread.sleep(1);
        var t2 = TimeMillis.now();

        assertThat(t2.isAfter(t1), is(true));
    }
}
