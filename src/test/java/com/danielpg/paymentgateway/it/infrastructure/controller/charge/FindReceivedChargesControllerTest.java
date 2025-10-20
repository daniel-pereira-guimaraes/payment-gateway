package com.danielpg.paymentgateway.it.infrastructure.controller.charge;

import com.danielpg.paymentgateway.ResourceLoader;
import com.danielpg.paymentgateway.it.infrastructure.controller.ControllerTestBase;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FindReceivedChargesControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/charges/received";

    private static final String EXPECTED_ALL_CHARGES_RESPONSE =
            ResourceLoader.load("/api/charges-received-expected-all-charges-response.json");

    private static final String EXPECTED_PAID_CANCELED_RESPONSE =
            ResourceLoader.load("/api/charges-received-expected-paid-canceled-charges-response.json");

    @Test
    void findsAllChargesWhenNoStatusesProvided() throws Exception {
        var responseContent = mockMvc.perform(get(ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, userToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(EXPECTED_ALL_CHARGES_RESPONSE, responseContent, true);
    }

    @Test
    void findsPaidAndCanceledChargesOnly() throws Exception {
        var responseContent = mockMvc.perform(get(ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, userToken())
                        .param("statuses", "PAID,CANCELED")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(EXPECTED_PAID_CANCELED_RESPONSE, responseContent, true);
    }

    @Test
    void returnsForbiddenWhenUserIsUnauthenticated() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsBadRequestWhenStatusIsInvalid() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, userToken())
                        .param("statuses", "INVALID")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
