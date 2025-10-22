package com.danielpg.paymentgateway.it.infrastructure.controller.charge;

import com.danielpg.paymentgateway.domain.charge.ChargeId;
import com.danielpg.paymentgateway.domain.charge.ChargeRepository;
import com.danielpg.paymentgateway.domain.charge.ChargeStatus;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CreateChargeControllerTest extends ControllerTestBase {

    private static final String REQUEST_BODY = """
            {
                "payerCpf": "%s",
                "amount": %s,
                "description": "%s"
            }
            """;

    private static final String DESCRIPTION = "Cobrança de teste";
    private static final String ENDPOINT = "/charges";

    private static final String VALID_PAYER_CPF = "32132132178";
    private static final String INVALID_PAYER_CPF = "456";
    private static final String NON_EXISTENT_PAYER_CPF = "00000099970";
    private static final long PAYER_ID_EXPECTED = 2L;

    private static final String AMOUNT = "150.01";
    private static final String NEGATIVE_AMOUNT = "-50.00";

    private static final String EMPTY_DESCRIPTION = "";

    @Autowired
    private ChargeRepository chargeRepository;

    @Test
    void createsChargeSuccessfullyWhenUserIsAuthenticated() throws Exception {
        var requestBody = REQUEST_BODY.formatted(VALID_PAYER_CPF, AMOUNT, DESCRIPTION);

        var response = new JSONObject(
                mockMvc.perform(post(ENDPOINT)
                                .header(AUTHORIZATION, userToken())
                                .content(requestBody).contentType(APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString()
        );

        var chargeId = ChargeId.of(response.getLong("id"));

        var charge = chargeRepository.get(chargeId).orElseThrow();
        assertThat(charge, notNullValue());
        assertThat(charge.issuerId().value(), is(CURRENT_USER.id().value()));
        assertThat(charge.payerId().value(), is(PAYER_ID_EXPECTED));
        assertThat(charge.amount().value(), comparesEqualTo(new BigDecimal(AMOUNT)));
        assertThat(charge.description().value(), is(DESCRIPTION));
        assertThat(charge.status(), is(ChargeStatus.PENDING));
    }

    @Test
    void returnsUnauthorizedWhenUserIsUnauthenticated() throws Exception {
        var requestBody = REQUEST_BODY.formatted(VALID_PAYER_CPF, AMOUNT, DESCRIPTION);

        mockMvc.perform(post(ENDPOINT)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsNotFoundWhenPayerDoesNotExist() throws Exception {
        var requestBody = REQUEST_BODY.formatted(NON_EXISTENT_PAYER_CPF, AMOUNT, DESCRIPTION);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsBadRequestWhenDataIsInvalid() throws Exception {
        var requestBody = REQUEST_BODY.formatted(INVALID_PAYER_CPF, NEGATIVE_AMOUNT, EMPTY_DESCRIPTION);

        mockMvc.perform(post(ENDPOINT)
                        .header(AUTHORIZATION, userToken())
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
