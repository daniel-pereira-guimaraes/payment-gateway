package com.danielpg.paymentgateway.it.infrastructure.integration;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import com.danielpg.paymentgateway.fixture.DepositFixture;
import com.danielpg.paymentgateway.infrastructure.integration.PaymentAuthorizerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

import static com.danielpg.paymentgateway.infrastructure.integration.PaymentAuthorizerImpl.Data;
import static com.danielpg.paymentgateway.infrastructure.integration.PaymentAuthorizerImpl.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentAuthorizerImplTest {

    @Value("${payment.authorizer.url}")
    private String authorizerUrl;

    @Autowired
    private PaymentAuthorizerImpl authorizer;

    @MockBean
    private RestTemplate restTemplate;

    private Charge charge;
    private CreditCard creditCard;
    private Deposit deposit;

    @BeforeEach
    void beforeEach() {
        charge = ChargeFixture.builder().build();
        creditCard = CreditCardFixture.builder().build();
        deposit = DepositFixture.builder().build();
    }

    @Test
    void authorizePaymentSuccessfully() {
        var response = new Response("success", new Data(true));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "PAYMENT")),
                eq(Response.class))).thenReturn(response);

        assertDoesNotThrow(() -> authorizer.authorizePayment(charge, creditCard));
    }

    @Test
    void throwsExceptionWhenPaymentIsNotAuthorized() {
        var response = new Response("success", new Data(false));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "PAYMENT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizePayment(charge, creditCard));
        assertThat(ex.getMessage(), is("Pagamento não autorizado."));
    }

    @Test
    void throwExceptionWhenNullResponseForPayment() {
        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "PAYMENT")),
                eq(Response.class))).thenReturn(null);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizePayment(charge, creditCard));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    @Test
    void throwExceptionWhenAuthorizedFieldIsNullForPayment() {
        var response = new Response("success", new Data(null));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "PAYMENT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizePayment(charge, creditCard));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    @Test
    void authorizeCancellationSuccessfully() {
        var response = new Response("success", new Data(true));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "CANCEL_PAYMENT")),
                eq(Response.class))).thenReturn(response);

        assertDoesNotThrow(() -> authorizer.authorizeCancellation(charge, creditCard));
    }

    @Test
    void throwsExceptionWhenCancellationIsNotAuthorized() {
        var response = new Response("success", new Data(false));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "CANCEL_PAYMENT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeCancellation(charge, creditCard));
        assertThat(ex.getMessage(), is("Cancelamento não autorizado."));
    }

    @Test
    void throwExceptionWhenNullResponseForCancellation() {
        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "CANCEL_PAYMENT")),
                eq(Response.class))).thenReturn(null);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeCancellation(charge, creditCard));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    @Test
    void throwExceptionWhenAuthorizedFieldIsNullForCancellation() {
        var response = new Response("success", new Data(null));

        when(restTemplate.getForObject(eq(expectedUri(charge.amount().value(), creditCard, "CANCEL_PAYMENT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeCancellation(charge, creditCard));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    @Test
    void authorizeDepositSuccessfully() {
        var response = new Response("success", new Data(true));

        when(restTemplate.getForObject(eq(expectedUri(deposit.amount().value(), null, "DEPOSIT")),
                eq(Response.class))).thenReturn(response);

        assertDoesNotThrow(() -> authorizer.authorizeDeposit(deposit));
    }

    @Test
    void throwsExceptionWhenDepositIsNotAuthorized() {
        var response = new Response("success", new Data(false));

        when(restTemplate.getForObject(eq(expectedUri(deposit.amount().value(), null, "DEPOSIT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeDeposit(deposit));
        assertThat(ex.getMessage(), is("Depósito não autorizado."));
    }

    @Test
    void throwExceptionWhenNullResponseForDeposit() {
        when(restTemplate.getForObject(eq(expectedUri(deposit.amount().value(), null, "DEPOSIT")),
                eq(Response.class))).thenReturn(null);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeDeposit(deposit));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    @Test
    void throwExceptionWhenAuthorizedFieldIsNullForDeposit() {
        var response = new Response("success", new Data(null));

        when(restTemplate.getForObject(eq(expectedUri(deposit.amount().value(), null, "DEPOSIT")),
                eq(Response.class))).thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeDeposit(deposit));
        assertThat(ex.getMessage(), is("Resposta inesperada do autorizador."));
    }

    private URI expectedUri(BigDecimal amount, CreditCard card, String operationType) {
        var builder = UriComponentsBuilder.fromUriString(authorizerUrl)
                .queryParam("amount", amount)
                .queryParam("operationType", operationType);

        if (card != null) {
            builder.queryParam("cardNumber", card.number().value())
                    .queryParam("cardExpiration", card.expirationDate().value())
                    .queryParam("cardCvv", card.cvv().value());
        }

        return builder.build().toUri();
    }
}