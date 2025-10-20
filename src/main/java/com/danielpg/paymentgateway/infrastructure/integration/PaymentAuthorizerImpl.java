package com.danielpg.paymentgateway.infrastructure.integration;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentAuthorizer;
import com.danielpg.paymentgateway.domain.charge.payment.PaymentNotAuthorizedException;
import com.danielpg.paymentgateway.domain.deposit.Deposit;
import com.danielpg.paymentgateway.domain.shared.creditcard.CreditCard;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
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
        authorizeGeneric(charge.id().value(), charge.amount().value(), creditCard, OperationType.PAYMENT);
    }

    @Override
    public void authorizeCancellation(Charge charge, CreditCard creditCard) {
        /*
        IMPORTANTE!

        Em um sistema real, não deveríamos salvar os dados do cartão
        em nosso banco de dados, mas apenas um ID da autorização, que posteriormente
        poderia ser usada aqui para pedir autorização para cancelamento.

        Como este é um projeto apenas para estudo, deixei os dados do cartão
        no banco de dados e estou passando-os ao solicitar autorização
        para cancelamento.
         */
        authorizeGeneric(charge.id().value(), charge.amount().value(), creditCard, OperationType.CANCEL_PAYMENT);
    }

    @Override
    public void authorizeDeposit(Deposit deposit) {
        authorizeGeneric(deposit.userId().value(), deposit.amount().value(), null, OperationType.DEPOSIT);
    }

    private void authorizeGeneric(Long id, BigDecimal amount, CreditCard creditCard, OperationType operation) {
        LOGGER.info("Consultando autorizador: id={}, operation={}", id, operation);
        validateAuthorizerUrl();

        var finalUri = buildUri(amount, creditCard, operation);
        LOGGER.info("{}", finalUri);
        var response = restTemplate.getForObject(finalUri, Response.class);

        if (response == null || response.data == null || response.data.authorized == null) {
            LOGGER.info("Resposta inesperada: {}", response);
            throw new PaymentNotAuthorizedException("Resposta inesperada do autorizador.");
        }

        if (!response.data.authorized) {
            throw new PaymentNotAuthorizedException(switch (operation) {
                case PAYMENT -> "Pagamento não autorizado.";
                case CANCEL_PAYMENT -> "Cancelamento não autorizado.";
                case DEPOSIT -> "Depósito não autorizado.";
            });
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

    private URI buildUri(BigDecimal amount, CreditCard creditCard, OperationType operation) {
        var builder = UriComponentsBuilder.fromUriString(authorizerUrl)
                .queryParam("amount", amount)
                .queryParam("operationType", operation.name());

        if (creditCard != null) {
            builder.queryParam("cardNumber", creditCard.number().value())
                    .queryParam("cardExpiration", creditCard.expirationDate().value())
                    .queryParam("cardCvv", creditCard.cvv().value());
        }

        return builder.build().toUri();
    }

    public record Response(String status, Data data) {
    }

    public record Data(Boolean authorized) {
    }

    private enum OperationType {
        PAYMENT,
        CANCEL_PAYMENT,
        DEPOSIT
    }
}
