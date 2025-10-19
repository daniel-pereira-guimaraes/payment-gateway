package com.danielpg.paymentgateway.it.infrastructure.controller.charge.payment;

import com.danielpg.paymentgateway.domain.charge.Charge;
import com.danielpg.paymentgateway.domain.charge.payment.*;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegisterPaymentControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/payments";

    private static final long CHARGE_ID_SUCCESS = 4L;
    private static final long CHARGE_ID_FORBIDDEN = 1L;
    private static final long CHARGE_ID_PAID = 8L;
    private static final long CHARGE_ID_CANCELED = 3L;
    private static final long CHANGE_ID_INSUFFICIENT_BALANCE = 9L;

    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CARD_EXPIRATION = "12/30";
    private static final String CARD_CVV = "123";

    private static final String REQUEST_WITH_BALANCE = """
            {
                "chargeId": %d,
                "method": "BALANCE",
                "creditCard": null
            }
            """;

    private static final String REQUEST_WITH_CARD = """
            {
                "chargeId": %d,
                "method": "CREDIT_CARD",
                "creditCard": {
                    "number": "%s",
                    "expirationDate": "%s",
                    "cvv": "%s"
                }
            }
            """;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentAuthorizer paymentAuthorizer;

    @Test
    void registersPaymentSuccessfullyWithBalance() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHARGE_ID_SUCCESS);

        var response = new JSONObject(
                mockMvc.perform(post(ENDPOINT)
                                .header(AUTHORIZATION, userToken())
                                .content(requestBody).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString()
        );

        var payment = paymentRepository.get(PaymentId.of(response.getLong("id"))).orElseThrow();
        assertThat(payment.chargeId().value(), is(CHARGE_ID_SUCCESS));
        assertThat(payment.method(), is(PaymentMethod.BALANCE));
        assertThat(payment.paidAt().value(), notNullValue());
    }

    @Test
    void registersPaymentSuccessfullyWithCreditCard() throws Exception {
        var requestBody = REQUEST_WITH_CARD.formatted(
                CHARGE_ID_SUCCESS, CARD_NUMBER, CARD_EXPIRATION, CARD_CVV);

        var response = new JSONObject(
                mockMvc.perform(post(ENDPOINT)
                                .header(AUTHORIZATION, userToken())
                                .content(requestBody).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString()
        );

        var payment = paymentRepository.get(PaymentId.of(response.getLong("id"))).orElseThrow();
        assertThat(payment.chargeId().value(), is(CHARGE_ID_SUCCESS));
        assertThat(payment.method(), is(PaymentMethod.CREDIT_CARD));
        assertThat(payment.paidAt().value(), notNullValue());
    }

    @Test
    void returnsForbiddenWhenUserIsNotPayer() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHARGE_ID_FORBIDDEN);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsForbiddenWhenUserIsUnauthenticated() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHARGE_ID_SUCCESS);

        mockMvc.perform(post(ENDPOINT)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsConflictWhenChargeIsPaid() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHARGE_ID_PAID);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsConflictWhenChargeIsCanceled() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHARGE_ID_CANCELED);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsConflictWhenInsufficientBalance() throws Exception {
        var requestBody = REQUEST_WITH_BALANCE.formatted(CHANGE_ID_INSUFFICIENT_BALANCE);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsConflictWhenCreditCardPaymentIsNotAuthorized() throws Exception {
        var requestBody = REQUEST_WITH_CARD.formatted(
                CHARGE_ID_SUCCESS, CARD_NUMBER, CARD_EXPIRATION, CARD_CVV);

        doThrow(PaymentNotAuthorizedException.class).when(paymentAuthorizer).authorizePayment(any());

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

}
