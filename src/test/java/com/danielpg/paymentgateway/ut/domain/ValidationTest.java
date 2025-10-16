package com.danielpg.paymentgateway.ut.domain;

import com.danielpg.paymentgateway.domain.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationTest {

    private static final String MESSAGE = "message";

    @Test
    void mustReturnValueWhenItIsNotNull() {
        var value = "any";

        var result = Validation.required(value, MESSAGE);

        assertThat(result, is(value));
    }

    @Test
    void throwsExceptionWhenValueIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.required(null, MESSAGE)
        );

        assertThat(exception.getMessage(), is(MESSAGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void throwsExceptionWhenValueIsBlankString(String value) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.required(value, MESSAGE)
        );

        assertThat(exception.getMessage(), is(MESSAGE));
    }

}
