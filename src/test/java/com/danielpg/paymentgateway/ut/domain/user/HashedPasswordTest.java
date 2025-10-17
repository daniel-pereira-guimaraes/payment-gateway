package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.HashedPassword;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HashedPasswordTest {

    private static final String NON_BLANK_HASH = "hash123";

    @Test
    void ofReturnsHashedPasswordWhenValid() {
        var hashedPassword = HashedPassword.of(NON_BLANK_HASH);

        assertThat(hashedPassword.hash(), is(NON_BLANK_HASH));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void ofThrowsExceptionWhenHashIsBlank(String invalidHash) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                HashedPassword.of(invalidHash)
        );
        assertThat(exception.getMessage(), containsString("O hash da senha é requerido."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void ofNullableReturnsEmptyForBlank(String blankHash) {
        var result = HashedPassword.ofNullable(blankHash);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void ofNullableReturnsOptionalWithValueForValidHash() {
        var result = HashedPassword.ofNullable(NON_BLANK_HASH);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().hash(), is(NON_BLANK_HASH));
    }

}
