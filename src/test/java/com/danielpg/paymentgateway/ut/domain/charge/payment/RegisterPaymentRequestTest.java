package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentRequest;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import org.junit.jupiter.api.Test;

import static com.danielpg.paymentgateway.fixture.ChargeFixture.CHARGE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterPaymentRequestTest {

    @Test
    void buildRequestWithBalanceSuccessfully() {
        var request = RegisterPaymentRequest.builder()
                .withChargeId(CHARGE_ID)
                .withMethod(PaymentMethod.BALANCE)
                .build();

        assertThat(request.chargeId(), is(CHARGE_ID));
        assertThat(request.method(), is(PaymentMethod.BALANCE));
        assertThat(request.creditCard(), nullValue());
    }

    @Test
    void buildRequestWithCreditCardSuccessfully() {
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withChargeId(CHARGE_ID)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        assertThat(request.chargeId(), is(CHARGE_ID));
        assertThat(request.method(), is(PaymentMethod.CREDIT_CARD));
        assertThat(request.creditCard(), is(creditCard));
    }

    @Test
    void throwsExceptionWhenChargeIdIsNull() {
        var builder = RegisterPaymentRequest.builder()
                .withChargeId(null)
                .withMethod(PaymentMethod.BALANCE);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("O id da cobrança é requerido."));
    }

    @Test
    void throwsExceptionWhenMethodIsNull() {
        var builder = RegisterPaymentRequest.builder()
                .withChargeId(CHARGE_ID)
                .withMethod(null);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("O método de pagamento é requerido."));
    }

    @Test
    void throwsExceptionWhenCreditCardIsNullForCreditCardMethod() {
        var builder = RegisterPaymentRequest.builder()
                .withChargeId(CHARGE_ID)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(null);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("O cartão de crédito é requerido para pagamentos com cartão."));
    }

    @Test
    void creditCardIsNullWhenMethodIsBalanceEvenIfProvided() {
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withChargeId(CHARGE_ID)
                .withMethod(PaymentMethod.BALANCE)
                .withCreditCard(creditCard)
                .build();

        assertThat(request.creditCard(), nullValue());
    }
}
