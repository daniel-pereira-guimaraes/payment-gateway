package com.danielpg.paymentgateway.infrastructure.integration;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;

@Component
public class PaymentAuthorizerImpl implements PaymentAuthorizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentAuthorizerImpl.class);

    @Value("${payment.authorizer.url}")
    private String authorizerUrl;

    private final RestTemplate restTemplate;

    public PaymentAuthorizerImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void authorizePayment(Charge charge, CreditCard creditCard) {
        LOGGER.info("Consultando autorizador: chargeId={}", charge.id());
        validateAuthorizerUrl();

        var finalUri = buildFinalUri(charge, creditCard);
        LOGGER.info("{}", finalUri); // TODO: remover!
        var response = restTemplate.getForObject(finalUri, Response.class);

        if (response == null || response.data == null || response.data.authorized == null) {
            LOGGER.info("Resposta inesperada: {}", response);
            throw new PaymentNotAuthorizedException("Resposta inesperada do autorizador.");
        }

        if (!response.data.authorized) {
            throw new PaymentNotAuthorizedException("Pagamento não autorizado.");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void validateAuthorizerUrl() {
        if (StringUtils.isBlank(authorizerUrl)) {
            throw new RuntimeException("URL do autorizador não configurada.");
        }
        try {
            URI.create(authorizerUrl).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL do autorizador inválida.", e);
        }
    }

    private URI buildFinalUri(Charge charge, CreditCard creditCard) {
        return UriComponentsBuilder.fromUriString(authorizerUrl)
                .queryParam("amount", charge.amount().value())
                .queryParam("cardNumber", creditCard.number().value())
                .queryParam("cardExpiration", creditCard.expirationDate().value())
                .queryParam("cardCvv", creditCard.cvv().value())
                .build()
                .toUri();
    }

    public record Response(String status, Data data) {}
    public record Data(Boolean authorized) {}
}
