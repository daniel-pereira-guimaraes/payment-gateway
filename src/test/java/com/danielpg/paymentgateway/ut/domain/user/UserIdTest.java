package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void ofReturnsUserIdWhenValid(long value) {
        var userId = UserId.of(value);

        assertThat(userId, notNullValue());
        assertThat(userId.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
    void ofThrowsExceptionWhenIdIsNotPositive(long invalidValue) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                UserId.of(invalidValue)
        );
        assertThat(exception.getMessage(), is("O id deve ser positivo."));
    }

    @Test
    void ofThrowsExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                UserId.of(null)
        );
        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = UserId.ofNullable(42L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(42L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = UserId.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        var id1 = UserId.of(10L);
        var id2 = UserId.of(10L);
        var id3 = UserId.of(20L);

        assertThat(id1, is(id2));
        assertThat(id1.hashCode(), is(id2.hashCode()));

        assertThat(id1, not(id3));
        assertThat(id1.hashCode(), not(id3.hashCode()));
    }
}
