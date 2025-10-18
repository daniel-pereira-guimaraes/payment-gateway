package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.InvalidPasswordException;
import com.danielpg.paymentgateway.domain.user.PlainTextPassword;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlainTextPasswordTest {

    private static final String VALID_PASSWORD = "Abcdef123!";
    private static final String MIN_LENGTH_PASSWORD = "A1a!bcdefg";
    private static final String MAX_LENGTH_PASSWORD = "A1a!" + "X".repeat(46);
    private static final String NO_UPPERCASE_PASSWORD = "a1!bcdefgh";
    private static final String NO_LOWERCASE_PASSWORD = "A1!BCDEFGH";
    private static final String NO_DIGIT_PASSWORD = "Aa!bcdefgh";
    private static final String NO_SYMBOL_PASSWORD = "Aa1bcdefgh";

    @Test
    void ofReturnsPlainTextPasswordWhenValid() {
        var plain = PlainTextPassword.of(VALID_PASSWORD);
        assertThat(plain.value(), is(VALID_PASSWORD));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void ofThrowsExceptionWhenPasswordIsBlank(String blank) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PlainTextPassword.of(blank)
        );
        assertThat(exception.getMessage(), is("A senha é requerida."));
    }

    @Test
    void ofThrowsExceptionWhenPasswordHasNoUppercase() {
        var exception = assertThrows(InvalidPasswordException.class, () ->
                PlainTextPassword.of(NO_UPPERCASE_PASSWORD)
        );
        assertThat(exception.getMessage(), is(
                PlainTextPassword.INVALID_PASSWORD_MESSAGE.formatted(10, 50)
        ));
    }

    @Test
    void ofThrowsExceptionWhenPasswordHasNoLowercase() {
        var exception = assertThrows(InvalidPasswordException.class, () ->
                PlainTextPassword.of(NO_LOWERCASE_PASSWORD)
        );
        assertThat(exception.getMessage(), is(
                PlainTextPassword.INVALID_PASSWORD_MESSAGE.formatted(10, 50)
        ));
    }

    @Test
    void ofThrowsExceptionWhenPasswordHasNoDigit() {
        var exception = assertThrows(InvalidPasswordException.class, () ->
                PlainTextPassword.of(NO_DIGIT_PASSWORD)
        );
        assertThat(exception.getMessage(), is(
                PlainTextPassword.INVALID_PASSWORD_MESSAGE.formatted(10, 50)
        ));
    }

    @Test
    void ofThrowsExceptionWhenPasswordHasNoSymbol() {
        var exception = assertThrows(InvalidPasswordException.class, () ->
                PlainTextPassword.of(NO_SYMBOL_PASSWORD)
        );
        assertThat(exception.getMessage(), is(
                PlainTextPassword.INVALID_PASSWORD_MESSAGE.formatted(10, 50)
        ));
    }

    @Test
    void ofReturnsPlainTextPasswordForMinLength() {
        var plain = PlainTextPassword.of(MIN_LENGTH_PASSWORD);
        assertThat(plain.value().length(), is(10));
        assertThat(plain.value(), is(MIN_LENGTH_PASSWORD));
    }

    @Test
    void ofReturnsPlainTextPasswordForMaxLength() {
        var plain = PlainTextPassword.of(MAX_LENGTH_PASSWORD);
        assertThat(plain.value().length(), is(50));
        assertThat(plain.value(), is(MAX_LENGTH_PASSWORD));
    }

}
