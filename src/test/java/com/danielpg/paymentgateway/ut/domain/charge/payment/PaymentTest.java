package com.danielpg.paymentgateway.ut.domain.charge.payment;

import org.junit.jupiter.api.Test;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.CHARGE_ID;
import static com.danielpg.paymentgateway.fixture.PaymentFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentTest {

    @Test
    void createPaymentSuccessfully() {
        var payment = builder().build();

        assertThat(payment, notNullValue());
        assertThat(payment.id(), is(PAYMENT_ID));
        assertThat(payment.chargeId(), is(CHARGE_ID));
        assertThat(payment.paidAt(), is(PAID_AT));
    }

    @Test
    void throwsExceptionWhenChargeIdIsNull() {
        var builder = builder().withChargeId(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O id da cobrança é requerido."));
    }

    @Test
    void throwsExceptionWhenPaidAtIsNull() {
        var builder = builder().withPaidAt(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("A data/hora do pagamento é requerida."));
    }

    @Test
    void finalizeCreationSuccessfully() {
        var payment = builder().withId(null).build();
        assertThat(payment.id(), nullValue());

        payment.finalizeCreation(PAYMENT_ID);

        assertThat(payment.id(), is(PAYMENT_ID));
    }

    @Test
    void cannotFinalizeCreationWhenAlreadyFinalized() {
        var payment = builder().build();
        assertThat(payment.id(), notNullValue());

        var exception = assertThrows(IllegalStateException.class,
                () -> payment.finalizeCreation(PAYMENT_ID)
        );

        assertThat(exception.getMessage(), is("A criação do pagamento já foi finalizada."));
    }

    @Test
    void cannotFinalizeCreationWithNullId() {
        var payment = builder().withId(null).build();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> payment.finalizeCreation(null)
        );

        assertThat(exception.getMessage(), is("O id é requerido."));
    }

}
