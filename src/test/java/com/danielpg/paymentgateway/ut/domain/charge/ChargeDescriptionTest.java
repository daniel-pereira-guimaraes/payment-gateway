package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.charge.ChargeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChargeDescriptionTest {

    private static final String VALID_VALUE = "Compra de papel";

    @Test
    void createChargeDescriptionWhenValid() {
        var description = ChargeDescription.of(VALID_VALUE);

        assertThat(description.value(), is(VALID_VALUE));
    }

    @Test
    void trimValueWhenHasSpaces() {
        var description = ChargeDescription.of("   Teste   ");

        assertThat(description.value(), is("Teste"));
    }

    @Test
    void throwExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> ChargeDescription.of(null));
    }

    @Test
    void createChargeDescriptionAtMaxLength() {
        var value = "A".repeat(70);
        var description = ChargeDescription.of(value);
        assertThat(description.value(), is(value));
    }

    @Test
    void throwExceptionWhenValueTooLong() {
        var longValue = "A".repeat(101);

        var exception = assertThrows(IllegalArgumentException.class, () -> ChargeDescription.of(longValue));

        assertThat(exception.getMessage(), is("A descrição deve ter no máximo 100 caracteres."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void ofNullableReturnsOptionalEmptyForBlank(String blankValue) {
        var result = ChargeDescription.ofNullable(blankValue);
        assertThat(result, is(Optional.empty()));
    }

    @Test
    void ofNullableReturnsOptionalWithValueForValid() {
        var result = ChargeDescription.ofNullable(VALID_VALUE);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(VALID_VALUE));
    }

    @Test
    void equalsAndHashCode() {
        var desc1 = ChargeDescription.of("Compra A");
        var desc2 = ChargeDescription.of("Compra A");
        var desc3 = ChargeDescription.of("Compra B");

        assertThat(desc1, is(desc2));
        assertThat(desc1.hashCode(), is(desc2.hashCode()));
        assertThat(desc1, is(not(desc3)));
    }

}
