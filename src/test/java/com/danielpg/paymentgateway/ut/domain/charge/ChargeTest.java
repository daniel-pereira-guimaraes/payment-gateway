package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChargeTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "Valid description" })
    void builderCreatesChargeWithAllFields(String description) {
        var expectedDescription = StringUtils.isBlank(description) ? null : description.trim();

        var charge = ChargeFixture.builder()
                .withDescription(description)
                .build();

        assertThat(charge, notNullValue());
        assertThat(charge.id(), is(ChargeFixture.CHARGE_ID));
        assertThat(charge.issuerId(), is(ChargeFixture.ISSUER_ID));
        assertThat(charge.payerId(), is(ChargeFixture.PAYER_ID));
        assertThat(charge.amount(), is(ChargeFixture.AMOUNT));
        assertThat(charge.description(), is(expectedDescription));
    }

    @Test
    void finalizeCreationSetsIdWhenNotSet() {
        var charge = ChargeFixture.builder().withId(null).build();
        var newId = ChargeId.of(999L);

        charge.finalizeCreation(newId);

        assertThat(charge.id(), is(newId));
    }

    @Test
    void finalizeCreationThrowsExceptionIfIdAlreadySet() {
        var charge = ChargeFixture.builder().build();
        var exception = assertThrows(IllegalStateException.class, () ->
                charge.finalizeCreation(ChargeId.of(123L))
        );

        assertThat(exception.getMessage(), is("A criação da cobrança já foi finalizada."));
    }

    @ParameterizedTest
    @NullSource
    void finalizeCreationThrowsExceptionIfIdIsNull(ChargeId nullId) {
        var charge = ChargeFixture.builder().withId(null).build();

        var exception = assertThrows(IllegalArgumentException.class, () ->
                charge.finalizeCreation(nullId)
        );

        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void throwsExceptionIfIssuerIdIsNull() {
        var builder = ChargeFixture.builder().withIssuerId(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O emitente é requerido."));
    }

    @Test
    void throwsExceptionIfPayerIdIsNull() {
        var builder = ChargeFixture.builder().withPayerId(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O pagador é requerido."));
    }

    @Test
    void throwsExceptionIfAmountIsNull() {
        var builder = ChargeFixture.builder().withAmount(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O valor é requerido."));
    }

    @Test
    void descriptionIsTrimmed() {
        var builder = ChargeFixture.builder().withDescription("   some description   ");
        var charge = builder.build();

        assertThat(charge.description(), is("some description"));
    }
}
