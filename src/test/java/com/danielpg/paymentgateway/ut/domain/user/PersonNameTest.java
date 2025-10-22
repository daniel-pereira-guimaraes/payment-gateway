package com.danielpg.paymentgateway.ut.domain.user;

import com.danielpg.paymentgateway.domain.user.InvalidPersonNameException;
import com.danielpg.paymentgateway.domain.user.PersonName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", " Daniel "})
    void createPersonNameWhenValid(String value) {
        var name = PersonName.of(value);

        assertThat(name.value(), is(value.trim()));
    }

    @Test
    void throwsExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () ->
                PersonName.of(null)
        );
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = " ")
    void throwExceptionWhenNameIsBlank(String invalidName) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PersonName.of(invalidName)
        );
        assertThat(exception.getMessage(), is("O nome deve ter de 2 a 70 caracteres."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A"})
    void throwExceptionWhenNameTooShort(String shortName) {
        var exception = assertThrows(InvalidPersonNameException.class, () ->
                PersonName.of(shortName)
        );
        assertThat(exception.getMessage(), is("O nome deve ter de 2 a 70 caracteres."));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 70})
    void createPersonNameWhenAtLengthLimits(int length) {
        var value = "A".repeat(length);
        var name = PersonName.of(value);

        assertThat(name.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {71, 100})
    void throwExceptionWhenNameTooLong(int length) {
        var exception = assertThrows(InvalidPersonNameException.class, () ->
                PersonName.of("A".repeat(length))
        );
        assertThat(exception.getMessage(), is("O nome deve ter de 2 a 70 caracteres."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void ofNullableReturnsOptionalEmptyForBlank(String blankName) {
        var result = PersonName.ofNullable(blankName);
        assertThat(result.isEmpty(), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Daniel", "João", "Maria Clara"})
    void ofNullableReturnsOptionalWithValueForValidName(String validName) {
        var result = PersonName.ofNullable(validName);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(validName));
    }

    @Test
    void equalsAndHashCode() {
        var name1 = PersonName.of("Daniel");
        var name2 = PersonName.of("Daniel");
        var name3 = PersonName.of("João");

        assertThat(name1, is(name2));
        assertThat(name1.hashCode(), is(name2.hashCode()));
        assertThat(name1, is(not(name3)));
    }

}
