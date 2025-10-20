package com.danielpg.paymentgateway.it.infrastructure.integration;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import com.danielpg.paymentgateway.fixture.ChargeFixture;
import com.danielpg.paymentgateway.fixture.CreditCardFixture;
import com.danielpg.paymentgateway.infrastructure.integration.PaymentAuthorizerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentAuthorizerImplTest {

    private static final String EXPECTED_URI_PATTERN = "%s?amount=%s&cardNumber=%s&cardExpiration=%s&cardCvv=%s";

    @Value("${payment.authorizer.url}")
    private String authorizerUrl;

    @Autowired
    private PaymentAuthorizerImpl authorizer;

    @MockBean
    private RestTemplate restTemplate;

    private Charge charge;
    private CreditCard creditCard;

    @BeforeEach
    void beforeEach() {
        charge = ChargeFixture.builder().build();
        creditCard = CreditCardFixture.builder().build();
    }

    @Test
    void authorizePaymentSuccessfully() {
        var response = new PaymentAuthorizerImpl.Response(
                "success", new PaymentAuthorizerImpl.Data(true));

        when(restTemplate.getForObject(
                eq(expectedPaymentUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> authorizer.authorizePayment(charge, creditCard));
    }

    @Test
    void throwsExceptionWhenPaymentIsNotAuthorized() {
        var response = new PaymentAuthorizerImpl.Response(
                "success", new PaymentAuthorizerImpl.Data(false)
        );

        when(restTemplate.getForObject(
                eq(expectedPaymentUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizePayment(charge, creditCard));

        assertEquals("Pagamento não autorizado.", ex.getMessage());
    }

    @Test
    void throwExceptionWhenNullResponseForPayment() {
        when(restTemplate.getForObject(
                eq(expectedPaymentUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(null);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizePayment(charge, creditCard));

        assertEquals("Resposta inesperada do autorizador.", ex.getMessage());
    }

    @Test
    void authorizeCancellationSuccessfully() {
        var response = new PaymentAuthorizerImpl.Response(
                "success", new PaymentAuthorizerImpl.Data(true));

        when(restTemplate.getForObject(
                eq(expectedCancelUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> authorizer.authorizeCancellation(charge, creditCard));
    }

    @Test
    void throwsExceptionWhenCancellationIsNotAuthorized() {
        var response = new PaymentAuthorizerImpl.Response(
                "success", new PaymentAuthorizerImpl.Data(false)
        );

        when(restTemplate.getForObject(
                eq(expectedCancelUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(response);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeCancellation(charge, creditCard));

        assertEquals("Cancelamento não autorizado.", ex.getMessage());
    }

    @Test
    void throwExceptionWhenNullResponseForCancellation() {
        when(restTemplate.getForObject(
                eq(expectedCancelUri()),
                eq(PaymentAuthorizerImpl.Response.class)))
                .thenReturn(null);

        var ex = assertThrows(PaymentNotAuthorizedException.class,
                () -> authorizer.authorizeCancellation(charge, creditCard));

        assertEquals("Resposta inesperada do autorizador.", ex.getMessage());
    }

    private URI expectedPaymentUri() {
        return URI.create(EXPECTED_URI_PATTERN.formatted(
                authorizerUrl,
                charge.amount().value().toString(),
                creditCard.number().value(),
                creditCard.expirationDate().value(),
                creditCard.cvv().value()
        ));
    }

    private URI expectedCancelUri() {
        return UriComponentsBuilder.fromUri(expectedPaymentUri())
                .queryParam("cancel", true)
                .build()
                .toUri();
    }
}
