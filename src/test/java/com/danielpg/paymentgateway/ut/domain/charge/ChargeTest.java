package com.danielpg.paymentgateway.ut.domain.charge;

import com.danielpg.paymentgateway.domain.shared.TimeMillis;
import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChargeTest {

    @Test
    void builderCreatesChargeWithAllFields() {
        var charge = builder().build();

        assertThat(charge, notNullValue());
        assertThat(charge.id(), is(ChargeFixture.CHARGE_ID));
        assertThat(charge.issuerId(), is(ChargeFixture.ISSUER_ID));
        assertThat(charge.payerId(), is(ChargeFixture.PAYER_ID));
        assertThat(charge.amount(), is(ChargeFixture.AMOUNT));
        assertThat(charge.description(), is(DESCRIPTION));
        assertThat(charge.createdAt(), is(CREATED_AT));
        assertThat(charge.dueAt(), is(DUE_AT));
        assertThat(charge.status(), is(ChargeStatus.PENDING));
    }

    @Test
    void finalizeCreationSetsIdWhenNotSet() {
        var charge = builder().withId(null).build();
        var newId = ChargeId.of(999L);

        charge.finalizeCreation(newId);

        assertThat(charge.id(), is(newId));
    }

    @Test
    void finalizeCreationThrowsExceptionIfIdAlreadySet() {
        var charge = builder().build();
        var exception = assertThrows(IllegalStateException.class, () ->
                charge.finalizeCreation(ChargeId.of(123L))
        );

        assertThat(exception.getMessage(), is("A criação da cobrança já foi finalizada."));
    }

    @ParameterizedTest
    @NullSource
    void finalizeCreationThrowsExceptionIfIdIsNull(ChargeId nullId) {
        var charge = builder().withId(null).build();

        var exception = assertThrows(IllegalArgumentException.class, () ->
                charge.finalizeCreation(nullId)
        );

        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void throwsExceptionIfIssuerIdIsNull() {
        var builder = builder().withIssuerId(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O emitente é requerido."));
    }

    @Test
    void throwsExceptionIfPayerIdIsNull() {
        var builder = builder().withPayerId(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O pagador é requerido."));
    }

    @Test
    void throwsExceptionIfAmountIsNull() {
        var builder = builder().withAmount(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O valor é requerido."));
    }

    @Test
    void throwsExceptionIfCreatedAtIsNull() {
        var builder = builder().withCreatedAt(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("A data/hora de criação é requerida."));
    }

    @Test
    void throwsExceptionIfDueAtIsNull() {
        var builder = builder().withDueAt(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("A data/hora de vencimento é requerida."));
    }

    @Test
    void throwsExceptionWhenDueAtIsBeforeCreatedAt() {
        var builder = builder()
                .withCreatedAt(TimeMillis.of(10L))
                .withDueAt(TimeMillis.of(9L));

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("A data/hora de vencimento deve ser posterior à data de criação."));
    }

    @Test
    void throwsExceptionWhenDueAtEqualsCreatedAt() {
        var builder = builder()
                .withCreatedAt(TimeMillis.of(10L))
                .withDueAt(TimeMillis.of(10L));

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("A data/hora de vencimento deve ser posterior à data de criação."));
    }

    @Test
    void throwsExceptionIfStatusIsNull() {
        var builder = builder().withStatus(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O status da cobrança é requerido."));
    }

    @Test
    void throwsExceptionWhenIssuerEqualsPayer() {
        var builder = ChargeFixture.builder().withIssuerId(ISSUER_ID).withPayerId(ISSUER_ID);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O devedor não pode ser igual ao emitente."));
    }

    @Test
    void changesStatusToPaidSuccessfullyWhenPending() {
        var charge = ChargeFixture.builder()
                .withStatus(ChargeStatus.PENDING)
                .build();

        charge.changeStatusToPaid();

        assertThat(charge.status(), is(ChargeStatus.PAID));
    }

    @ParameterizedTest
    @EnumSource(value = ChargeStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void changesStatusToPaidThrowsExceptionWhenNotPending(ChargeStatus initialStatus) {
        var charge = ChargeFixture.builder()
                .withStatus(initialStatus)
                .build();

        var exception = assertThrows(IllegalStateException.class,
                charge::changeStatusToPaid
        );

        assertThat(exception.getMessage(), is("A cobrança não está pendente."));
    }

    @ParameterizedTest
    @EnumSource(value = ChargeStatus.class, names = "CANCELED", mode = EnumSource.Mode.EXCLUDE)
    void changesStatusToCanceledSuccessfullyWhenNotCanceled(ChargeStatus initialStatus) {
        var charge = ChargeFixture.builder()
                .withStatus(initialStatus)
                .build();

        charge.changeStatusToCanceled();

        assertThat(charge.status(), is(ChargeStatus.CANCELED));
    }

    @Test
    void changesStatusToCanceledThrowsExceptionWhenCanceled() {
        var charge = ChargeFixture.builder()
                .withStatus(ChargeStatus.CANCELED)
                .build();

        var exception = assertThrows(IllegalStateException.class,
                charge::changeStatusToCanceled
        );

        assertThat(exception.getMessage(), is("A cobrança já está cancelada."));
    }

    @Test
    void ensurePendingStatusDoesNotThrowWhenPending() {
        var charge = ChargeFixture.builder()
                .withStatus(ChargeStatus.PENDING)
                .build();

        charge.ensurePendingStatus();
    }

    @ParameterizedTest
    @EnumSource(value = ChargeStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
    void ensurePendingStatusThrowsExceptionWhenNotPending(ChargeStatus nonPendingStatus) {
        var charge = ChargeFixture.builder()
                .withStatus(nonPendingStatus)
                .build();

        var exception = assertThrows(IllegalStateException.class, charge::ensurePendingStatus);
        assertThat(exception.getMessage(), is("A cobrança não está pendente."));
    }

    @ParameterizedTest
    @EnumSource(value = ChargeStatus.class, names = "CANCELED", mode = EnumSource.Mode.EXCLUDE)
    void ensureNotCanceledStatusDoesNotThrowWhenNotCanceled(ChargeStatus status) {
        var charge = ChargeFixture.builder().withStatus(status).build();

        charge.ensureNotCanceledStatus();
    }

    @Test
    void ensureNotCanceledStatusThrowsExceptionWhenCanceled() {
        var charge = ChargeFixture.builder().withStatus(ChargeStatus.CANCELED).build();

        var exception = assertThrows(IllegalStateException.class, charge::ensureNotCanceledStatus);
        assertThat(exception.getMessage(), is("A cobrança já está cancelada."));
    }

    @Test
    void equalsAndHashCodeWithSameData() {
        var charge1 = ChargeFixture.builder().build();
        var charge2 = ChargeFixture.builder().build();

        assertThat(charge1.equals(charge2), is(true));
        assertThat(charge1.hashCode(), is(charge2.hashCode()));
    }

    @Test
    void equalsAndHashCodeWithDifferentData() {
        var charge1 = ChargeFixture.builder().withId(ChargeId.of(1L)).build();
        var charge2 = ChargeFixture.builder().withId(ChargeId.of(2L)).build();

        assertThat(charge1.equals(charge2), is(false));
    }

}
