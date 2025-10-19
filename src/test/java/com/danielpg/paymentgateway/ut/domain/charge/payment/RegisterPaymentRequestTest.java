package com.danielpg.paymentgateway.ut.domain.charge.payment;

import com.danielpg.paymentgateway.domain.charge.payment.PaymentMethod;
import com.danielpg.paymentgateway.domain.charge.payment.RegisterPaymentRequest;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterPaymentRequestTest {

    @Test
    void createsRequestWithBalanceWhenPaymentMethodIsBalance() {
        var charge = ChargeFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.BALANCE)
                .build();

        assertThat(request.charge(), is(charge));
        assertThat(request.method(), is(PaymentMethod.BALANCE));
        assertThat(request.creditCard(), nullValue());
    }

    @Test
    void createsRequestWithCreditCardWhenPaymentMethodIsCreditCard() {
        var charge = ChargeFixture.builder().build();
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(creditCard)
                .build();

        assertThat(request.charge(), is(charge));
        assertThat(request.method(), is(PaymentMethod.CREDIT_CARD));
        assertThat(request.creditCard(), is(creditCard));
    }

    @Test
    void throwsExceptionWhenChargeIsNull() {
        var builder = RegisterPaymentRequest.builder()
                .withCharge(null)
                .withMethod(PaymentMethod.BALANCE);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("A cobrança é requerida."));
    }

    @Test
    void throwsExceptionWhenMethodIsNull() {
        var charge = ChargeFixture.builder().build();
        var builder = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(null);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("O método de pagamento é requerido."));
    }

    @Test
    void throwsExceptionWhenCreditCardIsNullForCreditCardMethod() {
        var charge = ChargeFixture.builder().build();
        var builder = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.CREDIT_CARD)
                .withCreditCard(null);

        var exception = assertThrows(NullPointerException.class, builder::build);
        assertThat(exception.getMessage(), is("O cartão de crédito é requerido para pagamentos com cartão."));
    }

    @Test
    void creditCardIsNullWhenMethodIsBalanceEvenIfProvided() {
        var charge = ChargeFixture.builder().build();
        var creditCard = CreditCardFixture.builder().build();
        var request = RegisterPaymentRequest.builder()
                .withCharge(charge)
                .withMethod(PaymentMethod.BALANCE)
                .withCreditCard(creditCard)
                .build();

        assertThat(request.creditCard(), nullValue());
    }
}
